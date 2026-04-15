# IMS – Task 1 Design Report

> **Changelog from doubt-doc clarifications:**  
> Responsibilities table updated with 6 new classes.  
> SD-1 replaced: now covers the 2-level leave approval workflow (Q4 explicit requirement).  
> State diagram: ALUMNI state added (Q20).  
> Design narrative updated to mention Parent actor and SMSAlert separation.  
> Patterns/Reflection unchanged — still valid after redesign.

---

## Section 1: Responsibilities Table

| Class | Primary Responsibility |
|-------|------------------------|
| `User` | Abstract base consolidating identity, authentication, and profile management for every actor. |
| `Student` | Represents an enrolled learner; owns admission, academic, attendance, and fee data throughout the student lifecycle. |
| `Employee` | Abstract base for all staff; owns employment metadata, payroll linkage, and leave tracking. |
| `Teacher` | Specialised employee responsible for delivering subjects, marking attendance, and grading/inputting exam results. |
| `Admin` | Elevated employee managing system-wide governance: user creation, role assignment, payslip approval, leave L2 approval, SMS dispatch, and news publishing. |
| `Parent` | User subclass with read-only access to a linked student's attendance, exam results, and fee status; can receive messages. *(Q40)* |
| `AlumniProfile` | Holds the restricted feature set available to a graduated student: transcript access, degree verification, and contact update. *(Q20)* |
| `Role` | Defines a named set of permissions assignable to any user, enabling role-based access control. |
| `Permission` | The atomic unit of access control — one authorised action on one resource. |
| `AdmissionForm` | Captures and validates all data for a student's admission including admin-created custom fields; records who created the template. *(Q44)* |
| `EducationHistory` | Prior academic qualifications belonging to a student's admission form. |
| `Course` | Defines a programme of study with its subjects, grading system, and associated batches. |
| `Batch` | Groups students under a course for a specific academic period; carries branch for per-branch fee logic. *(Q35)* |
| `Subject` | A single taught unit within a course; tracks elective status and weekly hour allocation. |
| `Exam` | Defines an assessment event with type (quiz, re-exam, etc.), applicable audience, evaluation method, and optionally a question bank. *(Q8/Q24)* |
| `ExamQuestion` | A single question-answer pair owned by an exam, with marks and question type. *(Q8/Q24)* |
| `ExamResult` | Records a student's outcome for a specific exam and computes grade/GPA. |
| `ExamReport` | Aggregates exam results into statistical and graphical on-demand reports. |
| `AttendanceRecord` | Captures attendance status of one student for one subject on one date, marked by any Employee. *(Q9)* |
| `AttendanceReport` | Generates daily, monthly, or subject-wise attendance summaries with filter support. |
| `PayrollForm` | Holds the salary structure (basic, allowances, deductions) and derives net pay for an employee. |
| `Payslip` | Monthly pay statement for an employee; tracks approval/rejection by Admin. |
| `LeaveRequest` | Records an employee's leave application with a 2-level approval chain, each level independently tracking remarks and status. *(Q4)* |
| `FeeCategory` | Named fee type with amount, due date, branch, and program level to support per-branch fee schedules. *(Q35)* |
| `FeePayment` | Records a student's payment against a fee category; generates receipts and triggers ledger transactions. |
| `Transaction` | Unified ledger entry for all financial events (fee payments, payroll disbursements, incomes, expenses). |
| `SMSAlert` | Sends group or single-target SMS alerts independently of the messaging and news modules. *(Q5)* |
| `Message` | Private two-way communication between any two users (including Parent). *(Q36)* |
| `Broadcast` | One-to-many communication from Admin to targeted role groups (e.g., all of a batch). |
| `TimeTable` | Complete weekly schedule for a batch; enforces subject hour limits and teacher workload constraints; surfaces student personal notes. *(Q28)* |
| `TimeSlot` | Single scheduled period linking a subject, teacher, time, and room; can detect conflicts. |
| `PersonalSlotNote` | Student-added note or alert on a timetable slot; does not modify the official schedule. *(Q28)* |
| `NewsArticle` | Public institute news in rich-text format, visible to all users; owns its comment thread. *(Q36)* |
| `Comment` | Public user response to a news article; subject to Admin moderation. |
| `Dashboard` | Central navigational hub providing global search, notifications, news feed, language switching, and settings for all user roles. *(Q15, Q46)* |
| `SearchResult` | One entry returned by the global dashboard search; carries module, entity type, and navigation path. *(Q15)* |
| `Notification` | System-generated alert or reminder delivered to an individual user. |
| `SystemConfig` | Institute-wide settings (grading, ID prefix, currency, timezone, supported languages) managed via Dashboard. *(Q27)* |

---

## Section 2: Sequence Diagrams (Flow Descriptions)

> Each step format: `StepNo. Sender ──[FlowName : type]──► Receiver`  
> **Types:** `call` (initiating request), `return` (response), `create` (object instantiation), `async` (fire-and-forget).

---

### SD-1: Two-Level Leave Approval *(Q4 — explicit critical workflow)*

**Actors / Participants (left to right):** `Employee`, `LeaveRequest`, `HOD (Employee)`, `Admin (HR)`, `Notification`

**Pre-condition:** Employee is logged in. A supervisor (HOD) and a senior Admin are available as Level-1 and Level-2 approvers respectively.  
**Post-condition:** `LeaveRequest.overallStatus` = APPROVED; employee and approvers are notified.

| Step | Sender | Flow Name | Type | Receiver | Notes |
|------|--------|-----------|------|----------|-------|
| 1 | `Employee` | applyLeave(leaveType, fromDate, toDate, reason) | `call` | `LeaveRequest` | Employee submits a leave application |
| 2 | `LeaveRequest` | create(employeeId, leaveType, dates, reason) | `create` | `LeaveRequest` | LeaveRequest instantiated; `overallStatus` = PENDING_L1 |
| 3 | `LeaveRequest` | requestCreated : leaveId | `return` | `Employee` | Returns the new leave request ID |
| 4 | `LeaveRequest` | dispatch(hodId, "Leave application pending L1 review") | `async` | `Notification` | Notifies the Level-1 approver (HOD) |
| 5 | `HOD` | getLeaveRequests() | `call` | `HOD` | HOD views pending leave applications *(self-call / internal fetch)* |
| 6 | `HOD` | approveLevel1(leaveId, remarks) | `call` | `LeaveRequest` | HOD approves with remarks; alternatively calls `rejectLevel1()` |
| 7 | `LeaveRequest` | setLevel1(approverId, remarks, APPROVED) | `call` | `LeaveRequest` | Sets `level1Status` = APPROVED; `overallStatus` = PENDING_L2 *(self-call)* |
| 8 | `LeaveRequest` | level1Updated | `return` | `HOD` | Confirms L1 decision recorded |
| 9 | `LeaveRequest` | dispatch(hrAdminId, "Leave pending L2 review") | `async` | `Notification` | Notifies the Level-2 approver (HR/Admin) |
| 10 | `Admin` | approveLeaveLevel2(leaveId, remarks) | `call` | `LeaveRequest` | Admin approves with remarks; alternatively calls `rejectLevel2()` |
| 11 | `LeaveRequest` | setLevel2(approverId, remarks, APPROVED) | `call` | `LeaveRequest` | Sets `level2Status` = APPROVED; `overallStatus` = APPROVED; calls `isFullyApproved()` *(self-call)* |
| 12 | `LeaveRequest` | level2Updated | `return` | `Admin` | Confirms L2 decision recorded |
| 13 | `LeaveRequest` | dispatch(employeeId, "Leave approved") | `async` | `Notification` | Notifies the employee of final approval |

> **Alt flow (rejection at L1):** Step 6 calls `rejectLevel1(leaveId, remarks)` → `overallStatus` = REJECTED_L1 → Step 13 fires notification to employee. Sequence terminates; L2 is never reached.  
> **Alt flow (rejection at L2):** L1 is approved; Step 10 calls `rejectLevel2()` → `overallStatus` = REJECTED_L2 → notification to employee.

---

### SD-2: Fee Payment Processing

**Actors / Participants (left to right):** `Admin`, `FeeCategory`, `FeePayment`, `Transaction`, `Notification`

**Pre-condition:** A `FeeCategory` with the correct `branch` and `programLevel` exists. Student is enrolled.  
**Post-condition:** Payment recorded, receipt generated, ledger entry created, student notified.

| Step | Sender | Flow Name | Type | Receiver | Notes |
|------|--------|-----------|------|----------|-------|
| 1 | `Admin` | selectStudent(studentId) | `call` | `Admin` | Admin locates student via dashboard search *(internal)* |
| 2 | `Admin` | getFeeCategories(branch, programLevel) | `call` | `FeeCategory` | Retrieves applicable categories by branch + program level *(Q35)* |
| 3 | `FeeCategory` | categoryList | `return` | `Admin` | Returns pending categories with amounts and due dates |
| 4 | `Admin` | initiatePayment(studentId, categoryId, amount, mode) | `call` | `FeePayment` | Admin opens a new payment entry |
| 5 | `FeePayment` | create(studentId, categoryId, amount, mode) | `create` | `FeePayment` | FeePayment object instantiated |
| 6 | `FeePayment` | processPayment() | `call` | `FeePayment` | Validates amount; marks as paid *(self-call)* |
| 7 | `FeePayment` | record(INCOME, amount, paymentId) | `call` | `Transaction` | Creates ledger entry |
| 8 | `Transaction` | transactionId | `return` | `FeePayment` | Returns transaction reference |
| 9 | `FeePayment` | generateReceipt() | `call` | `FeePayment` | Produces receipt file *(self-call)* |
| 10 | `FeePayment` | receiptFile | `return` | `Admin` | Receipt returned for print/download |
| 11 | `FeePayment` | dispatch(studentId, "Payment confirmed") | `async` | `Notification` | Notifies student |
| 12 | `Admin` | checkDefaulters(branch, programLevel) | `call` | `FeeCategory` | Optionally refreshes defaulters list |
| 13 | `FeeCategory` | defaulterList | `return` | `Admin` | Updated list of students yet to pay |

---

### SD-3: Teacher Marks Attendance

**Actors / Participants (left to right):** `Teacher`, `Batch`, `AttendanceRecord`, `AttendanceReport`, `SMSAlert`, `Notification`

**Pre-condition:** Teacher is logged in with an active time slot for the batch.  
**Post-condition:** All records saved; low-attendance students receive an in-app notification and optionally an SMS alert.

| Step | Sender | Flow Name | Type | Receiver | Notes |
|------|--------|-----------|------|----------|-------|
| 1 | `Teacher` | getAssignedBatches() | `call` | `Teacher` | Teacher retrieves batch list *(self-call on association)* |
| 2 | `Teacher` | getStudents() | `call` | `Batch` | Retrieves student roster for selected batch |
| 3 | `Batch` | studentList | `return` | `Teacher` | Returns list of students |
| 4 | `Teacher` | mark(studentId, subjectId, date, status, remarks) | `call` | `AttendanceRecord` | *[Loop: one call per student]* Creates one record |
| 5 | `AttendanceRecord` | create(…) | `create` | `AttendanceRecord` | AttendanceRecord instantiated |
| 6 | `AttendanceRecord` | recordSaved | `return` | `Teacher` | Confirms persistence |
| 7 | `Teacher` | finaliseSession() | `call` | `AttendanceReport` | Signals end of session; triggers report refresh |
| 8 | `AttendanceReport` | getAttendancePercentage(studentId) | `call` | `AttendanceReport` | *[Loop: per student]* Recomputes running percentage *(self-call)* |
| 9 | `AttendanceReport` | sessionSummary | `return` | `Teacher` | Returns today's session summary |
| 10 | `AttendanceReport` | dispatch(studentId, "Low attendance warning") | `async` | `Notification` | *[If %  < threshold]* In-app alert to student |
| 11 | `AttendanceReport` | send(SMSTargetType.SINGLE, studentPhone, alertMsg) | `async` | `SMSAlert` | *[Optional, if SMS enabled in SystemConfig]* Q5: fires single SMS alert to the affected student's phone *(Q5)* |

---

## Section 3: State Diagram – Student Lifecycle

**Entity:** `Student`

---

### States

| State | Description |
|-------|-------------|
| `APPLIED` | Admission form submitted, pending review |
| `ADMITTED` | Approved; unique studentId generated; batch not yet assigned |
| `ENROLLED` | Assigned to a batch and course; timetable active |
| `ACTIVE` | Normal academic state: attending classes, sitting exams, paying fees |
| `SUSPENDED` | Temporarily restricted (fee default or disciplinary) |
| `BATCH_TRANSFERRED` | Mid-state during a batch transfer |
| `GRADUATED` | Course successfully completed; AlumniProfile created |
| `ALUMNI` | Post-graduation state with restricted login access *(Q20)* |
| `WITHDRAWN` | Voluntarily exited before completion |
| `REJECTED` | Admission rejected; terminal |

---

### State Transition Diagram

```
                   [Form submitted]
  ○ ─────────────────────────────────► APPLIED
                                           │
                ┌──────────────────────────┤
                │ [Approved /              │ [Rejected /
                │  ID generated]           │  invalid]
                ▼                          ▼
            ADMITTED                   REJECTED (terminal)
                │
                │ [Batch assigned]
                ▼
            ENROLLED
                │
                │ [Session starts]
                ▼
             ACTIVE ◄──────────────────────────────────────┐
                │                                          │
     ┌──────────┼──────────────┬──────────────────┐        │
     │          │              │                  │        │
     │ [Fee     │ [Transfer    │ [All credits      │        │
     │  default /│  requested] │  completed]      │        │
     │  discip.] │             │                  │        │
     ▼           ▼             ▼                  │        │
 SUSPENDED  BATCH_TRANSFERRED  GRADUATED          │        │
     │           │               │                │        │
     │ [Issue    │ [New batch     │ [alumniProfile  │        │
     │  resolved]│  confirmed]   │  created]      │        │
     └──────────►└───────────────┘                │        │
     [back to ACTIVE]        ▼                    │        │
                           ALUMNI ◄───────────────┘        │
                           (restricted login)              │
                                                           │
              BATCH_TRANSFERRED ──────────────────────────►┘
              [on new batch confirmation → ACTIVE]

                    │ [Voluntary exit]
                    ▼
                WITHDRAWN (terminal)
```

---

### Guard Conditions & Actions

| From | Event / Guard | Action on Transition | To |
|------|--------------|----------------------|----|
| `APPLIED` | Approved \[form valid\] | Generate studentId, create Student record | `ADMITTED` |
| `APPLIED` | Rejected \[invalid / quota full\] | Notify applicant | `REJECTED` |
| `ADMITTED` | Batch assigned | Link to Batch; activate Timetable | `ENROLLED` |
| `ENROLLED` | Session starts | Enable attendance, exam, fee modules | `ACTIVE` |
| `ACTIVE` | Fee default OR disciplinary flag | Restrict portal access | `SUSPENDED` |
| `SUSPENDED` | Issue resolved | Restore portal access | `ACTIVE` |
| `ACTIVE` | Transfer requested \[target batch has capacity\] | Detach from old batch | `BATCH_TRANSFERRED` |
| `BATCH_TRANSFERRED` | New batch confirmed | Attach to new batch; rebuild timetable | `ACTIVE` |
| `ACTIVE` | All credits completed \[pass conditions met\] | Issue graduation record | `GRADUATED` |
| `GRADUATED` | Alumni profile created | Restrict to alumni feature set | `ALUMNI` |
| `ACTIVE` | Voluntary exit submitted | Archive student record | `WITHDRAWN` |

---

## Section 4: Design Narrative

### Low Coupling

Dependencies between modules are mediated through IDs (foreign-key style references) rather than direct object graphs. `FeePayment` stores `studentId: String` and `categoryId: String`, not live references to `Student` or `FeeCategory`. The Finance module can therefore evolve independently of the Admission or Student modules. The same holds for `LeaveRequest` — it stores `level1ApproverId` and `level2ApproverId` as Strings rather than holding `Employee` objects. The `Parent` actor accesses student data only via explicitly defined methods (`viewAttendance`, `viewExamResults`) rather than a direct object reference to `Student`, keeping the parent-facing interface narrow and stable. The new `SMSAlert` class is similarly self-contained — it does not reach into `AttendanceRecord` or `Message`; the caller (Admin or AttendanceReport) constructs and fires it independently.

### High Cohesion

Each class owns a single, well-bounded slice of behaviour. `LeaveRequest` exclusively manages the two-level approval lifecycle; it does not touch payroll or attendance. `SMSAlert` is solely concerned with SMS dispatch and delivery reporting — it has no knowledge of messaging or news internals. `AlumniProfile` manages only the post-graduation feature subset; the parent `Student` record stays intact. No class is a catch-all utility. `Admin` comes closest but its operations are deliberately restricted to system-governance actions (user creation, role assignment, approvals, SMS dispatch, news publishing) rather than domain business logic.

### Separation of Concerns

Three orthogonal tiers are maintained:
- **Data ownership** — `Student`, `Employee`, `Parent`, `Course`, `Batch` own raw domain data.
- **Process / lifecycle** — `AdmissionForm`, `LeaveRequest`, `Payslip`, `FeePayment`, `AlumniProfile` model transactional workflows with explicit status machines.
- **Reporting / analytics** — `ExamReport`, `AttendanceReport` handle aggregation and export independently of the records they read.

The `Dashboard` is purely navigational. `SystemConfig` is the single place where institute-wide settings live — language switching, SMS toggle, and grading system all flow through it, keeping per-module classes free of configuration logic. The `SMSAlert` module is a separate concern from `Message` and `Broadcast` (Q5/Q36).

### Law of Demeter

No method chains through an intermediate object to reach a third. `Teacher` calls `getAssignedBatches()` on itself, then calls `getStudents()` on the returned `Batch` — it does not call `batch.getStudents().get(0).getAttendanceRecords()`. Where a parent needs student data, the `Parent` class exposes `viewAttendance(studentId)` directly rather than handing out a `Student` object for the caller to navigate. In the leave approval flow, `Admin` calls `approveLeaveLevel2(leaveId, remarks)` on `LeaveRequest` directly — it does not reach through `Employee.getLeaveHistory().get(n)` to mutate a nested object.

---

## Section 5: Design Patterns & Anti-Patterns

### Design Patterns Present

#### 1. Strategy Pattern — Examination Evaluation Method

**Where:** `Exam` holds `evaluationMethod: EvalMethod` (GPA, CCE, CWA), and `ExamResult.calculateGrade()` delegates the calculation to the strategy selected at exam-creation time.

**Why it fits:** Different institutes use different grading methods. Adding a new evaluation scheme (e.g., letter-grade only) requires only a new concrete strategy — `Exam` and `ExamResult` are closed for modification. This satisfies the Open/Closed Principle. With Q8/Q24 additions (`ExamType` variants, `ExamApplicability`), the Strategy pattern also cleanly governs *who* gets which evaluation method without branching logic inside `Exam`.

**Participants:** Context = `Exam`, Strategy interface = `EvalMethod`, Concrete strategies = GPA calculator, CCE calculator, CWA calculator.

---

#### 2. Observer Pattern — Notification & SMS Dispatch

**Where:** After state-changing operations (leave fully approved, payment processed, low attendance detected), the originating class fires an `async` dispatch to `Notification` and optionally to `SMSAlert`. The source publishes an event; `Notification`/`SMSAlert` are independent subscribers.

**Why it fits:** The notification/SMS concern is entirely decoupled from the business logic that triggers it. `LeaveRequest` does not know *how* notifications are delivered. New subscribers (email service, push gateway) can be added without touching source classes. The separation between `Notification` (in-app) and `SMSAlert` (SMS channel, Q5) is exactly the multiple-subscriber pattern in practice.

**Participants:** Publishers = `LeaveRequest`, `FeePayment`, `AttendanceReport`; Subscribers = `Notification`, `SMSAlert`.

---

### Anti-Patterns Identified & Avoided

#### 1. God Object (Avoided)

**Risk:** The `Dashboard` is the single entry point to the application and could have been designed to own all business logic — admission processing, fee computation, leave approval, timetable management.

**How it was avoided:** `Dashboard` is deliberately thin: it exposes `search()`, `navigate()`, `getNotifications()`, `getLatestNews()`, and `updateSettings()` only. Every substantive operation is delegated to the module that owns it. With the `SearchResult` class now defined (Q15), even search navigation is handled by `SearchResult.navigate()` rather than by `Dashboard` itself. `Dashboard` does not reach into `FeePayment`, `LeaveRequest`, or `Exam` logic directly.

**Consequence of not avoiding it:** Dashboard would accumulate hundreds of methods across all modules, becoming untestable and impossible to maintain independently.

---

#### 2. Anemic Domain Model (Avoided)

**Risk:** Classes are pure data holders (getters/setters only), with all logic pushed into external service classes.

**How it was avoided:** Domain objects carry meaningful behaviour. `LeaveRequest.isFullyApproved()` computes final status from its own two-level data. `TimeSlot.conflictsWith()` encodes scheduling logic. `Payslip.approve()` / `Payslip.reject()` manage the approval lifecycle. `ExamResult.isPassed()` computes pass/fail from its own marks. `AdmissionForm.validate()` enforces business rules. `AlumniProfile.verifyDegree()` encapsulates degree-verification logic. None of these behaviours are externally injected; they live where the data lives.

**Consequence of not avoiding it:** All business logic would leak into `Admin` or stateless utility classes, breaking cohesion and making the domain model impossible to test in isolation.

---

## Section 6: Design Reflection

### Two Strongest Aspects

#### 1. Clean Inheritance Hierarchy with Meaningful Specialisation

The `User → Employee → Teacher/Admin` and `User → Student/Parent` hierarchy is shallow (max two levels), avoiding the fragile base-class problem. The addition of `Parent extends User` (Q40) slotted in cleanly without touching any existing subclass. Each subclass adds genuinely distinct attributes and behaviours. `Teacher` adds teaching-specific behaviour; `Admin` adds governance-specific behaviour; `Parent` adds read-only child-monitoring behaviour. All are independently substitutable for `User` where needed (e.g., message delivery, notification dispatch). This makes the hierarchy stable and straightforward to extend further (e.g., `Librarian extends Employee`) without breaking existing code.

#### 2. Clear Separation Between Data Ownership, Process, and Reporting

The three-tier concern separation ensures that changes to the attendance-marking process (`AttendanceRecord`) do not ripple into the reporting layer (`AttendanceReport`), and vice versa. The same pattern holds for Exams (`ExamQuestion` / `ExamResult` / `ExamReport`) and Finance (`FeePayment` / `Transaction`). The 2-level leave approval redesign (Q4) reinforced this: all approval state is self-contained in `LeaveRequest`; `Admin` only calls methods on it without knowing the internal state machine. This makes each layer independently testable and limits the blast radius of any single change.

---

### Two Weakest Aspects

#### 1. `Admin` Class Risks Becoming Overloaded

`Admin` now carries eight distinct responsibilities: user creation, role assignment, news publishing, message broadcasting, payslip approval, 2-level leave approval, SMS dispatch, and admission template creation. Each of these could justify a dedicated service. The current design provides no clean mechanism to distribute these further without refactoring `Admin` significantly. A future improvement would be to introduce dedicated classes — `PayrollManager`, `LeaveApprovalManager`, `SMSService` — that `Admin` delegates to, making `Admin` a thin orchestrator rather than a direct implementor.

#### 2. No Common Interface for Reporting Classes

`ExamReport` and `AttendanceReport` share structural similarities (`generate()`, `export()`, `filters`) but have no common interface or abstract base class. Code that needs to handle reports generically (e.g., a bulk export scheduler or audit log) cannot do so polymorphically. Introducing a `Report <<interface>>` with `generate()` and `export(format: String): File` would unify them and allow the Open/Closed Principle to apply consistently here. This is the most actionable structural gap in the current design and would take minimal effort to fix.
