# IMS – UML Class Design Reference

> **Changelog from doubt-doc clarifications:**  
> Q4 → `LeaveRequest` redesigned for 2-level approval with remarks per level.  
> Q5 → `SMSAlert` class added as a separate SMS module (group / single targets).  
> Q8/Q24 → `Exam` gets `applicableTo` + `applicableGroup`; new `ExamQuestion` composition.  
> Q20 → `AlumniProfile` added for graduated students with restricted-access feature set.  
> Q28 → `PersonalSlotNote` added for student-added timetable alerts/notes.  
> Q35 → `FeeCategory` gets `branch` and `programLevel` for per-branch due dates.  
> Q40 → `Parent` added as a `User` subclass (parents have system login).  
> Q44 → `AdmissionForm` gets `templateCreatedBy` (Acads office creates the template).  
> Q15 → `SearchResult` class added to back the global dashboard search.  
> Q9  → Association 11 widened: attendance markedBy any `Employee`, not just `Teacher`.  
> Q36 → `NewsArticle` has no `targetRoles` — news is always public.  
> Enum fixes: `ExamType` + `LeaveStatus` updated; new `ExamApplicability`, `SMSTargetType`.

---

## Section 1: Classes, Attributes & Methods

> Convention: `+` public, `-` private, `#` protected  
> Abstract classes marked `<<abstract>>`. Types follow Java/Kotlin conventions.

---

### 1.1 `User` `<<abstract>>`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | userId | String | `-` |
| Attribute | name | String | `-` |
| Attribute | email | String | `-` |
| Attribute | passwordHash | String | `-` |
| Attribute | phone | String | `-` |
| Attribute | photoUrl | String | `-` |
| Attribute | isActive | Boolean | `-` |
| Attribute | createdAt | Date | `-` |
| Method | login(email: String, password: String) : Boolean | — | `+` |
| Method | logout() : void | — | `+` |
| Method | updateProfile(name: String, phone: String) : void | — | `+` |
| Method | changePassword(oldPwd: String, newPwd: String) : Boolean | — | `+` |
| Method | getRole() : Role | — | `+` |
| Method | generateUserId() : String | — | `#` |

---

### 1.2 `Student` *(extends User)*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | studentId | String | `-` |
| Attribute | dateOfBirth | Date | `-` |
| Attribute | gender | String | `-` |
| Attribute | address | String | `-` |
| Attribute | category | String | `-` |
| Attribute | isFormerStudent | Boolean | `-` |
| Attribute | admissionDate | Date | `-` |
| Attribute | isGraduated | Boolean | `-` |
| Method | getAdmissionForm() : AdmissionForm | — | `+` |
| Method | getAttendanceRecords() : List\<AttendanceRecord\> | — | `+` |
| Method | getExamResults() : List\<ExamResult\> | — | `+` |
| Method | transferBatch(newBatch: Batch) : void | — | `+` |
| Method | graduate() : void | — | `+` |
| Method | getFeePayments() : List\<FeePayment\> | — | `+` |
| Method | getAlumniProfile() : AlumniProfile | — | `+` |
| Method | addTimetableNote(note: PersonalSlotNote) : void | — | `+` |

---

### 1.3 `Employee` *(extends User)* `<<abstract>>`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | employeeId | String | `-` |
| Attribute | designation | String | `-` |
| Attribute | department | String | `-` |
| Attribute | dateOfJoining | Date | `-` |
| Attribute | salary | Double | `-` |
| Attribute | isActive | Boolean | `-` |
| Method | applyLeave(request: LeaveRequest) : void | — | `+` |
| Method | getPayslips() : List\<Payslip\> | — | `+` |
| Method | getLeaveHistory() : List\<LeaveRequest\> | — | `+` |
| Method | exit(exitDate: Date, reason: String) : void | — | `+` |

---

### 1.4 `Teacher` *(extends Employee)*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | subjectsTaught | List\<Subject\> | `-` |
| Attribute | maxWeeklyHours | Int | `-` |
| Method | markAttendance(record: AttendanceRecord) : void | — | `+` |
| Method | getAssignedBatches() : List\<Batch\> | — | `+` |
| Method | getTimetable() : TimeTable | — | `+` |
| Method | gradeExam(result: ExamResult) : void | — | `+` |
| Method | inputExamMarks(examId: String, studentId: String, marks: Double) : void | — | `+` |

---

### 1.5 `Admin` *(extends Employee)*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | accessLevel | Int | `-` |
| Method | createUser(user: User) : void | — | `+` |
| Method | assignRole(user: User, role: Role) : void | — | `+` |
| Method | publishNews(article: NewsArticle) : void | — | `+` |
| Method | broadcastMessage(broadcast: Broadcast) : void | — | `+` |
| Method | approvePayslip(payslip: Payslip) : void | — | `+` |
| Method | sendSMSAlert(alert: SMSAlert) : void | — | `+` |
| Method | approveLeaveLevel1(request: LeaveRequest, remarks: String) : void | — | `+` |
| Method | approveLeaveLevel2(request: LeaveRequest, remarks: String) : void | — | `+` |
| Method | createAdmissionTemplate(form: AdmissionForm) : void | — | `+` |

---

### 1.6 `Parent` *(extends User)*
> *New — Q40: parents have system login with read-only access to their child's data.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | parentId | String | `-` |
| Attribute | relation | String | `-` |
| Attribute | isEmergencyContact | Boolean | `-` |
| Attribute | address | String | `-` |
| Method | getLinkedStudents() : List\<Student\> | — | `+` |
| Method | viewAttendance(studentId: String) : List\<AttendanceRecord\> | — | `+` |
| Method | viewExamResults(studentId: String) : List\<ExamResult\> | — | `+` |
| Method | viewFeeStatus(studentId: String) : List\<FeePayment\> | — | `+` |
| Method | receiveMessage(msg: Message) : void | — | `+` |

---

### 1.7 `AlumniProfile`
> *New — Q20: graduated students retain login with a restricted feature subset (transcript access, degree verification, contact update).*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | alumniId | String | `-` |
| Attribute | studentId | String | `-` |
| Attribute | graduationDate | Date | `-` |
| Attribute | degree | String | `-` |
| Attribute | allowedFeatures | List\<String\> | `-` |
| Method | getTranscript() : File | — | `+` |
| Method | verifyDegree(verifierId: String) : Boolean | — | `+` |
| Method | updateContactInfo(phone: String, address: String) : void | — | `+` |

---

### 1.8 `Role`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | roleId | String | `-` |
| Attribute | roleName | String | `-` |
| Attribute | permissions | List\<Permission\> | `-` |
| Method | addPermission(p: Permission) : void | — | `+` |
| Method | removePermission(p: Permission) : void | — | `+` |
| Method | hasPermission(action: String) : Boolean | — | `+` |

---

### 1.9 `Permission`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | permissionId | String | `-` |
| Attribute | action | String | `-` |
| Attribute | resource | String | `-` |
| Method | getDescription() : String | — | `+` |

---

### 1.10 `AdmissionForm`
> *Q44: `templateCreatedBy` records that Acads office / Admin designs the form template; student/staff fills it.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | formId | String | `-` |
| Attribute | studentId | String | `-` |
| Attribute | templateCreatedBy | String | `-` |
| Attribute | customFields | Map\<String, String\> | `-` |
| Attribute | submittedAt | Date | `-` |
| Attribute | status | AdmissionStatus | `-` |
| Method | submit() : void | — | `+` |
| Method | validate() : Boolean | — | `+` |
| Method | addField(key: String, value: String) : void | — | `+` |
| Method | removeField(key: String) : void | — | `+` |
| Method | getEducationHistory() : List\<EducationHistory\> | — | `+` |

---

### 1.11 `EducationHistory`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | historyId | String | `-` |
| Attribute | institutionName | String | `-` |
| Attribute | qualification | String | `-` |
| Attribute | yearOfPassing | Int | `-` |
| Attribute | percentage | Double | `-` |
| Method | getSummary() : String | — | `+` |

---

### 1.12 `Course`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | courseId | String | `-` |
| Attribute | courseName | String | `-` |
| Attribute | duration | Int | `-` |
| Attribute | description | String | `-` |
| Attribute | gradingSystem | String | `-` |
| Method | getBatches() : List\<Batch\> | — | `+` |
| Method | getSubjects() : List\<Subject\> | — | `+` |
| Method | addSubject(s: Subject) : void | — | `+` |

---

### 1.13 `Batch`
> *Q35: `branch` field added for per-branch fee due-date logic.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | batchId | String | `-` |
| Attribute | batchName | String | `-` |
| Attribute | startDate | Date | `-` |
| Attribute | endDate | Date | `-` |
| Attribute | capacity | Int | `-` |
| Attribute | branch | String | `-` |
| Method | getStudents() : List\<Student\> | — | `+` |
| Method | getTimeTable() : TimeTable | — | `+` |
| Method | addStudent(s: Student) : void | — | `+` |
| Method | removeStudent(s: Student) : void | — | `+` |
| Method | getAssignedTeacher() : Teacher | — | `+` |

---

### 1.14 `Subject`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | subjectId | String | `-` |
| Attribute | subjectName | String | `-` |
| Attribute | isElective | Boolean | `-` |
| Attribute | weeklyHours | Int | `-` |
| Attribute | credits | Int | `-` |
| Method | getAssignedTeacher() : Teacher | — | `+` |
| Method | getExams() : List\<Exam\> | — | `+` |

---

### 1.15 `Exam`
> *Q8/Q24: `examType` expanded (QUIZ, RE_EXAM, etc.); `applicableTo` + `applicableGroup` define who can sit the exam; `allowDirectMarkEntry` supports direct marks/grade input without questions.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | examId | String | `-` |
| Attribute | examName | String | `-` |
| Attribute | examType | ExamType | `-` |
| Attribute | evaluationMethod | EvalMethod | `-` |
| Attribute | totalMarks | Double | `-` |
| Attribute | passingMarks | Double | `-` |
| Attribute | scheduledDate | Date | `-` |
| Attribute | examGroupId | String | `-` |
| Attribute | applicableTo | ExamApplicability | `-` |
| Attribute | applicableGroup | String | `-` |
| Attribute | allowDirectMarkEntry | Boolean | `-` |
| Method | getResults() : List\<ExamResult\> | — | `+` |
| Method | getQuestions() : List\<ExamQuestion\> | — | `+` |
| Method | addQuestion(q: ExamQuestion) : void | — | `+` |
| Method | generateReport() : ExamReport | — | `+` |
| Method | getStatistics() : ExamStats | — | `+` |
| Method | isApplicableTo(studentId: String) : Boolean | — | `+` |

---

### 1.16 `ExamQuestion`
> *New — Q8/Q24: faculty inputs questions and expected answers; supports multiple question types.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | questionId | String | `-` |
| Attribute | examId | String | `-` |
| Attribute | questionText | String | `-` |
| Attribute | answerText | String | `-` |
| Attribute | marks | Double | `-` |
| Attribute | questionType | QuestionType | `-` |
| Method | getMarks() : Double | — | `+` |
| Method | edit(questionText: String, answerText: String) : void | — | `+` |

---

### 1.17 `ExamResult`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | resultId | String | `-` |
| Attribute | studentId | String | `-` |
| Attribute | examId | String | `-` |
| Attribute | marksObtained | Double | `-` |
| Attribute | grade | String | `-` |
| Attribute | gpa | Double | `-` |
| Attribute | remarks | String | `-` |
| Method | calculateGrade() : String | — | `+` |
| Method | isPassed() : Boolean | — | `+` |

---

### 1.18 `ExamReport`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | reportId | String | `-` |
| Attribute | generatedAt | Date | `-` |
| Attribute | reportType | String | `-` |
| Attribute | filters | Map\<String, String\> | `-` |
| Method | generate() : void | — | `+` |
| Method | export(format: String) : File | — | `+` |
| Method | getChartData() : ChartData | — | `+` |

---

### 1.19 `AttendanceRecord`
> *Q9: `markedBy` association points to `Employee` (not just Teacher) — Admin can also mark attendance.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | recordId | String | `-` |
| Attribute | studentId | String | `-` |
| Attribute | subjectId | String | `-` |
| Attribute | date | Date | `-` |
| Attribute | status | AttendanceStatus | `-` |
| Attribute | remarks | String | `-` |
| Method | mark(status: AttendanceStatus) : void | — | `+` |
| Method | updateRemarks(text: String) : void | — | `+` |

---

### 1.20 `AttendanceReport`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | reportId | String | `-` |
| Attribute | reportType | ReportType | `-` |
| Attribute | fromDate | Date | `-` |
| Attribute | toDate | Date | `-` |
| Attribute | filters | Map\<String, String\> | `-` |
| Method | generate() : void | — | `+` |
| Method | getAttendancePercentage(studentId: String) : Double | — | `+` |
| Method | export(format: String) : File | — | `+` |

---

### 1.21 `PayrollForm`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | formId | String | `-` |
| Attribute | employeeId | String | `-` |
| Attribute | basicSalary | Double | `-` |
| Attribute | allowances | Map\<String, Double\> | `-` |
| Attribute | deductions | Map\<String, Double\> | `-` |
| Method | calculateNetPay() : Double | — | `+` |
| Method | validate() : Boolean | — | `+` |

---

### 1.22 `Payslip`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | payslipId | String | `-` |
| Attribute | employeeId | String | `-` |
| Attribute | month | Int | `-` |
| Attribute | year | Int | `-` |
| Attribute | netPay | Double | `-` |
| Attribute | status | PayslipStatus | `-` |
| Attribute | generatedAt | Date | `-` |
| Method | approve() : void | — | `+` |
| Method | reject(reason: String) : void | — | `+` |
| Method | export() : File | — | `+` |

---

### 1.23 `LeaveRequest`
> *Q4: Completely redesigned for 2-level approval. Level 1 = HOD / direct supervisor. Level 2 = HR / senior Admin. Each level independently records remarks and approval status. A request is fully approved only when both levels are APPROVED.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | leaveId | String | `-` |
| Attribute | employeeId | String | `-` |
| Attribute | leaveType | String | `-` |
| Attribute | fromDate | Date | `-` |
| Attribute | toDate | Date | `-` |
| Attribute | reason | String | `-` |
| Attribute | overallStatus | LeaveStatus | `-` |
| Attribute | level1ApproverId | String | `-` |
| Attribute | level1Remarks | String | `-` |
| Attribute | level1Status | LeaveApprovalStatus | `-` |
| Attribute | level2ApproverId | String | `-` |
| Attribute | level2Remarks | String | `-` |
| Attribute | level2Status | LeaveApprovalStatus | `-` |
| Method | approveLevel1(approverId: String, remarks: String) : void | — | `+` |
| Method | rejectLevel1(approverId: String, remarks: String) : void | — | `+` |
| Method | approveLevel2(approverId: String, remarks: String) : void | — | `+` |
| Method | rejectLevel2(approverId: String, remarks: String) : void | — | `+` |
| Method | getDuration() : Int | — | `+` |
| Method | isFullyApproved() : Boolean | — | `+` |

---

### 1.24 `FeeCategory`
> *Q35: `branch` + `programLevel` allow different due dates for different programs (e.g., UG fee due 10 May, PG due 12 May).*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | categoryId | String | `-` |
| Attribute | name | String | `-` |
| Attribute | amount | Double | `-` |
| Attribute | dueDate | Date | `-` |
| Attribute | branch | String | `-` |
| Attribute | programLevel | String | `-` |
| Attribute | isRecurring | Boolean | `-` |
| Method | getDefaulters() : List\<Student\> | — | `+` |

---

### 1.25 `FeePayment`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | paymentId | String | `-` |
| Attribute | studentId | String | `-` |
| Attribute | categoryId | String | `-` |
| Attribute | amountPaid | Double | `-` |
| Attribute | paidAt | Date | `-` |
| Attribute | paymentMode | String | `-` |
| Attribute | receiptNumber | String | `-` |
| Method | processPayment() : Boolean | — | `+` |
| Method | generateReceipt() : File | — | `+` |

---

### 1.26 `Transaction`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | transactionId | String | `-` |
| Attribute | type | TransactionType | `-` |
| Attribute | amount | Double | `-` |
| Attribute | description | String | `-` |
| Attribute | date | Date | `-` |
| Attribute | referenceId | String | `-` |
| Method | record() : void | — | `+` |
| Method | getLinkedPayslip() : Payslip | — | `+` |

---

### 1.27 `SMSAlert`
> *New — Q5: Standalone SMS module; separate from messaging and news. Supports group alerts (e.g., all of a batch) and single alerts (e.g., individual missed-attendance SMS).*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | alertId | String | `-` |
| Attribute | message | String | `-` |
| Attribute | targetType | SMSTargetType | `-` |
| Attribute | targetId | String | `-` |
| Attribute | sentAt | Date | `-` |
| Attribute | status | SMSStatus | `-` |
| Attribute | sentBy | String | `-` |
| Method | send() : void | — | `+` |
| Method | getRecipients() : List\<String\> | — | `+` |
| Method | getDeliveryReport() : Map\<String, Boolean\> | — | `+` |

---

### 1.28 `Message`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | messageId | String | `-` |
| Attribute | senderId | String | `-` |
| Attribute | receiverId | String | `-` |
| Attribute | content | String | `-` |
| Attribute | sentAt | Date | `-` |
| Attribute | isRead | Boolean | `-` |
| Method | send() : void | — | `+` |
| Method | markAsRead() : void | — | `+` |
| Method | delete() : void | — | `+` |

---

### 1.29 `Broadcast`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | broadcastId | String | `-` |
| Attribute | title | String | `-` |
| Attribute | content | String | `-` |
| Attribute | targetRoles | List\<String\> | `-` |
| Attribute | sentAt | Date | `-` |
| Method | send() : void | — | `+` |
| Method | getRecipientCount() : Int | — | `+` |

---

### 1.30 `TimeTable`
> *Q28: `getPersonalNotes()` surfaces student-added notes for a given slot.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | timetableId | String | `-` |
| Attribute | batchId | String | `-` |
| Attribute | effectiveFrom | Date | `-` |
| Attribute | effectiveTo | Date | `-` |
| Attribute | slots | List\<TimeSlot\> | `-` |
| Method | addSlot(slot: TimeSlot) : void | — | `+` |
| Method | removeSlot(slotId: String) : void | — | `+` |
| Method | editSlot(slotId: String, updated: TimeSlot) : void | — | `+` |
| Method | checkTeacherOverload(teacher: Teacher) : Boolean | — | `+` |
| Method | checkSubjectLimit(subject: Subject) : Boolean | — | `+` |
| Method | getPersonalNotes(studentId: String) : List\<PersonalSlotNote\> | — | `+` |

---

### 1.31 `TimeSlot`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | slotId | String | `-` |
| Attribute | dayOfWeek | String | `-` |
| Attribute | startTime | Time | `-` |
| Attribute | endTime | Time | `-` |
| Attribute | subjectId | String | `-` |
| Attribute | teacherId | String | `-` |
| Attribute | room | String | `-` |
| Attribute | durationMinutes | Int | `-` |
| Method | getDuration() : Int | — | `+` |
| Method | conflictsWith(other: TimeSlot) : Boolean | — | `+` |

---

### 1.32 `PersonalSlotNote`
> *New — Q28: Students can add personal reminders/notes to any timetable slot (e.g., "submit assignment", "revision session"). Does not affect the official timetable.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | noteId | String | `-` |
| Attribute | studentId | String | `-` |
| Attribute | slotId | String | `-` |
| Attribute | noteText | String | `-` |
| Attribute | alertTime | Time | `-` |
| Attribute | createdAt | Date | `-` |
| Method | setAlert(time: Time) : void | — | `+` |
| Method | edit(text: String) : void | — | `+` |
| Method | delete() : void | — | `+` |

---

### 1.33 `NewsArticle`
> *Q36: News is always publicly visible to all logged-in users. No `targetRoles` — targeting is a property of `Broadcast`, not `NewsArticle`.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | articleId | String | `-` |
| Attribute | title | String | `-` |
| Attribute | content | String | `-` |
| Attribute | authorId | String | `-` |
| Attribute | publishedAt | Date | `-` |
| Attribute | isPublished | Boolean | `-` |
| Attribute | tags | List\<String\> | `-` |
| Method | publish() : void | — | `+` |
| Method | edit(title: String, content: String) : void | — | `+` |
| Method | delete() : void | — | `+` |
| Method | getComments() : List\<Comment\> | — | `+` |

---

### 1.34 `Comment`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | commentId | String | `-` |
| Attribute | articleId | String | `-` |
| Attribute | authorId | String | `-` |
| Attribute | content | String | `-` |
| Attribute | postedAt | Date | `-` |
| Method | post() : void | — | `+` |
| Method | delete() : void | — | `+` |

---

### 1.35 `Dashboard`
> *Q15: `search()` returns `List<SearchResult>` — defined below. Q46: Dashboard is common to all user roles.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | userId | String | `-` |
| Attribute | language | String | `-` |
| Attribute | country | String | `-` |
| Attribute | currency | String | `-` |
| Attribute | timeZone | String | `-` |
| Method | search(query: String) : List\<SearchResult\> | — | `+` |
| Method | getLatestNews() : List\<NewsArticle\> | — | `+` |
| Method | getNotifications() : List\<Notification\> | — | `+` |
| Method | navigate(module: String) : void | — | `+` |
| Method | updateSettings(config: SystemConfig) : void | — | `+` |
| Method | switchLanguage(langCode: String) : void | — | `+` |

---

### 1.36 `SearchResult`
> *New — Q15: One result entry returned by the global dashboard search. Carries enough metadata to navigate directly to the matched entity.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | resultId | String | `-` |
| Attribute | label | String | `-` |
| Attribute | module | String | `-` |
| Attribute | entityType | String | `-` |
| Attribute | entityId | String | `-` |
| Attribute | navigationPath | String | `-` |
| Method | navigate() : void | — | `+` |
| Method | getDisplayText() : String | — | `+` |

---

### 1.37 `Notification`

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | notificationId | String | `-` |
| Attribute | recipientId | String | `-` |
| Attribute | message | String | `-` |
| Attribute | type | NotificationType | `-` |
| Attribute | isRead | Boolean | `-` |
| Attribute | createdAt | Date | `-` |
| Method | markAsRead() : void | — | `+` |
| Method | dismiss() : void | — | `+` |

---

### 1.38 `SystemConfig`
> *Q27: `supportedLanguages` list enables a language-switching mechanism even if full localisation is stubbed.*

| Kind | Name | Type | Access |
|------|------|------|--------|
| Attribute | configId | String | `-` |
| Attribute | gradingSystem | String | `-` |
| Attribute | autoIdPrefix | String | `-` |
| Attribute | currency | String | `-` |
| Attribute | timeZone | String | `-` |
| Attribute | country | String | `-` |
| Attribute | smsEnabled | Boolean | `-` |
| Attribute | supportedLanguages | List\<String\> | `-` |
| Method | save() : void | — | `+` |
| Method | reset() : void | — | `+` |

---

## Section 2: Inheritance Relationships

> Format: `Child ──▷ Parent`  (open arrowhead = generalization)

| # | Child Class | Parent Class | Notes |
|---|-------------|--------------|-------|
| 1 | `Student` | `User` | A Student *is-a* User with admission-specific attributes |
| 2 | `Employee` | `User` | An Employee *is-a* User; abstract — never instantiated directly |
| 3 | `Teacher` | `Employee` | A Teacher *is-a* Employee; teaches subjects, marks attendance |
| 4 | `Admin` | `Employee` | An Admin *is-a* Employee with elevated system governance privileges |
| 5 | `Parent` | `User` | A Parent *is-a* User with read-only access to child's academic data *(Q40)* |

```
User  <<abstract>>
 ├──▷ Student
 ├──▷ Parent
 └──▷ Employee  <<abstract>>
         ├──▷ Teacher
         └──▷ Admin
```

---

## Section 3: Association Relationships

> Associations represent a *uses / knows-about* relationship without ownership.

| # | Class A | Role A | Card. | Card. | Role B | Class B | Notes |
|---|---------|--------|:-----:|:-----:|--------|---------|-------|
| 1 | `User` | assignedRole | `1` | `1..*` | assignedTo | `Role` | Every user has at least one role |
| 2 | `Role` | carries | `1` | `0..*` | belongsTo | `Permission` | A role has zero or more permissions |
| 3 | `Student` | enrolledIn | `1..*` | `1` | contains | `Batch` | Many students per batch |
| 4 | `Batch` | under | `1..*` | `1` | offers | `Course` | Many batches per course |
| 5 | `Teacher` | teaches | `1..*` | `1..*` | taughtBy | `Subject` | Many-to-many |
| 6 | `Teacher` | assignedTo | `1..*` | `1..*` | hasTutor | `Batch` | Teacher across multiple batches |
| 7 | `Exam` | conductedFor | `1..*` | `1` | hasExams | `Subject` | Exam tied to one subject |
| 8 | `ExamResult` | recordedFor | `0..*` | `1` | sits | `Student` | Student has many results |
| 9 | `ExamResult` | resultOf | `0..*` | `1` | produces | `Exam` | Exam produces many results |
| 10 | `AttendanceRecord` | recordedFor | `0..*` | `1` | has | `Student` | Student has many records |
| 11 | `AttendanceRecord` | markedBy | `0..*` | `1` | marks | `Employee` | Any Employee marks attendance *(Q9)* |
| 12 | `AttendanceRecord` | inSubject | `0..*` | `1` | hasAttendance | `Subject` | Per-subject records |
| 13 | `Employee` | hasPayroll | `1` | `1` | paidVia | `PayrollForm` | Exactly one payroll form per employee |
| 14 | `Payslip` | generatedFor | `0..*` | `1` | receives | `Employee` | Employee receives many payslips |
| 15 | `FeePayment` | madeBy | `0..*` | `1` | makes | `Student` | Student makes many payments |
| 16 | `FeePayment` | under | `0..*` | `1` | classifies | `FeeCategory` | Payment falls under a category |
| 17 | `Message` | sentBy | `0..*` | `1` | sends | `User` | Any user sends messages |
| 18 | `Message` | receivedBy | `0..*` | `1` | receives | `User` | Any user (incl. Parent) receives messages |
| 19 | `NewsArticle` | writtenBy | `0..*` | `1` | authors | `Admin` | Only Admin publishes; news is public *(Q36)* |
| 20 | `Comment` | postedBy | `0..*` | `1` | posts | `User` | Any logged-in user comments |
| 21 | `Comment` | on | `0..*` | `1` | hasComments | `NewsArticle` | Article owns many comments |
| 22 | `TimeTable` | assignedTo | `1` | `1` | has | `Batch` | One timetable per batch |
| 23 | `TimeSlot` | partOf | `0..*` | `1` | contains | `TimeTable` | Timetable has many slots |
| 24 | `TimeSlot` | taughtBy | `0..*` | `1` | occupies | `Teacher` | Teacher occupies many slots |
| 25 | `Dashboard` | usedBy | `1` | `1` | accesses | `User` | Every user has a dashboard session |
| 26 | `Parent` | guardOf | `1..*` | `1..*` | guardedBy | `Student` | Parent linked to one or more students *(Q40)* |
| 27 | `Notification` | sentTo | `0..*` | `1` | receives | `User` | User receives many notifications |
| 28 | `Transaction` | linkedTo | `0..1` | `0..1` | generates | `Payslip` | Payslip may produce a transaction |
| 29 | `Transaction` | linkedTo | `0..1` | `0..1` | generates | `FeePayment` | Fee payment records a transaction |
| 30 | `Broadcast` | sentBy | `0..*` | `1` | broadcasts | `Admin` | Admin sends many broadcasts |
| 31 | `SMSAlert` | triggeredBy | `0..*` | `1` | sends | `Admin` | Admin triggers SMS alerts *(Q5)* |
| 32 | `LeaveRequest` | approvedAtL1By | `0..*` | `0..1` | approvesL1 | `Employee` | Level-1 approver (HOD) *(Q4)* |
| 33 | `LeaveRequest` | approvedAtL2By | `0..*` | `0..1` | approvesL2 | `Admin` | Level-2 approver (HR/senior Admin) *(Q4)* |
| 34 | `PersonalSlotNote` | addedBy | `0..*` | `1` | adds | `Student` | Student adds notes to slots *(Q28)* |
| 35 | `PersonalSlotNote` | attachedTo | `0..*` | `1` | hasNotes | `TimeSlot` | Slot has many personal notes |
| 36 | `AlumniProfile` | belongsTo | `1` | `1` | has | `Student` | One profile per graduated student *(Q20)* |

---

## Section 4: Composition Relationships

> The part is destroyed when the whole is destroyed (strong ownership).

| # | Whole | Role (Whole) | Card. | Card. | Role (Part) | Part | Rationale |
|---|-------|--------------|:-----:|:-----:|-------------|------|-----------|
| 1 | `AdmissionForm` | contains | `1` | `1..*` | partOf | `EducationHistory` | History records exist only inside a form |
| 2 | `TimeTable` | composes | `1` | `1..*` | partOf | `TimeSlot` | Slots have no existence outside their timetable |
| 3 | `Exam` | produces | `1` | `0..*` | partOf | `ExamResult` | Results are owned by their exam |
| 4 | `Exam` | generates | `1` | `0..1` | partOf | `ExamReport` | Report is owned by its exam |
| 5 | `Exam` | contains | `1` | `0..*` | partOf | `ExamQuestion` | Questions exist only within their exam *(Q8/Q24)* |
| 6 | `AttendanceReport` | aggregates | `1` | `1..*` | partOf | `AttendanceRecord` | Records scoped to a report run |
| 7 | `Course` | owns | `1` | `1..*` | partOf | `Subject` | Subjects meaningless without their course |
| 8 | `Course` | owns | `1` | `0..*` | partOf | `Batch` | Batches created under and owned by course |
| 9 | `Dashboard` | embeds | `1` | `1` | partOf | `SystemConfig` | Config owned and managed via Dashboard |
| 10 | `Employee` | owns | `1` | `0..*` | partOf | `LeaveRequest` | Leave requests belong to the employee entirely |
| 11 | `PayrollForm` | produces | `1` | `0..*` | partOf | `Payslip` | Payslips generated from payroll form |
| 12 | `Student` | owns | `1` | `1` | partOf | `AdmissionForm` | One form per student |
| 13 | `Student` | owns | `1` | `0..1` | partOf | `AlumniProfile` | Created on graduation; belongs to the student *(Q20)* |

---

## Enumerations (Supporting Types)

```
enum AdmissionStatus      { PENDING, APPROVED, REJECTED }

enum AttendanceStatus     { PRESENT, ABSENT, LATE, EXCUSED }

// Q8/Q24: Added QUIZ, IN_CLASS_ACTIVITY, RE_EXAM
enum ExamType             { MARKS_BASED, GRADE_BASED, QUIZ, IN_CLASS_ACTIVITY, RE_EXAM, CUSTOM }

enum EvalMethod           { GPA, CCE, CWA }

// Q8: who the exam applies to
enum ExamApplicability    { ALL, SPECIFIC_GROUP }

// Q24: question variants in an exam
enum QuestionType         { MCQ, DESCRIPTIVE, SHORT_ANSWER }

enum PayslipStatus        { PENDING, APPROVED, REJECTED }

// Q4: 2-level leave approval; overall status summarises both levels
enum LeaveStatus          { PENDING_L1, PENDING_L2, APPROVED, REJECTED_L1, REJECTED_L2 }
enum LeaveApprovalStatus  { PENDING, APPROVED, REJECTED }

enum TransactionType      { INCOME, EXPENSE, ASSET, LIABILITY, DONATION }

enum NotificationType     { INFO, ALERT, REMINDER }

enum ReportType           { DAILY, MONTHLY, SUBJECT_WISE }

// Q5: SMS module
enum SMSTargetType        { GROUP, SINGLE }
enum SMSStatus            { QUEUED, SENT, FAILED }
```
