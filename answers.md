# Answers

1. Is it enough to write documentation using Javadoc comments for the Codebase Documentation part of Task 3? Or is it necessary to maintain separate markdown files going over the codebase?
[DM] Separate docs are required. (Javadocs can be skipped but having them will make it easier for you in general)

2. For dashboard, should we have the features corresponding to the modules we selected or all?
[DM] Leaving that creative liberty upto you. You can use stubs for things you haven't done, as long as you can justify your dashboard desgin choices.

3. Can we deploy a server to handle APIs and data? If yes, can it use any other language or just jvm a based lang?
[DM] You can use a server or a dummy local database, upto you. No stack restrictions on the language for the server.

4. "It is also important that your prototype must follow the same workflow i.e support the exact same set of features as in our actual IMS system." Why is this requirement there? Are we supposed to design the system according to the rest of the doc, or this part here? If we are supposed to design according to the requirements mentioned in the doc, why is this confusing statement even there? If we are supposed to follow this and somehow figure out the entirety of IMS, why is the rest of the doc there? Clarify this.
[DM] This part applies to the implementation section only. All it means is for the features and workflows of our current IMS **you know**, must be there too (on top of addiontional features). For ex.: For putting a leave application, you apply for leave, and then it needs to get apporved by 2 different authorities with remarks. So you system for such a feature can't be just direct apporve reject but should support remarks by 2 different levels.

5. In `Dashboard`,
    - Customizable admission forms and SMS module integration for group/single alerts.
    So the SMS module is specifically for admissions? What are group/single alerts?

[DM] Custom admission forms and SMS modules are separate unrelated things. Group single alerts for example could be group for a part branch, batch, etc. while single alerts could be people who missed attendance on a particular day.

6. In `Dashboard`, these are the requirements:
    - Search bar for instant navigation
    - displays latest news on login
    - Language settings and basic configuration (country, currency, time zone).
    - General settings: grading systems, automatic unique IDs, etc.
    - Manage courses, batches, subjects (including electives), and batch transfers.
    - Customizable admission forms and SMS module integration for group/single alerts.
    - Manage student categories and graduation facilities.
    
Does end-to-end implementation of dashboard mean end-to-end implementation for all of the above features? So the entirety of grading systems must be implemented? Or is the grading system itself a small stub that is not required to be fully implemented for dashboard?
[DM] No. The features are only for the UML parts. For the actual dashboard you can desgin it however you want as long as you are able to justify the design and you can have stubs if you are putting the features you aren't implementing but want on the dashboard.

7. In `Admission`,
    - Fully customizable to meet specific school standards.

Does this mean that I need to accomodate different grading systems (for example, percentage grading systems, letter grading systems, GPA grading systems), or does this mean something else?
[DM] Allows you to make any custom form - that is the administartion can make forms with any custom fields they want.

8. In `Exams`,
    - Create exams based on grades, marks, or custom types; group exams as needed.

Am I suppose to implement a system that generates question and answers for an exam paper, based on the grades/ marks/custom types? What does this entire sentence mean?
[DM] Not generates, you can input the questions and answers. You can put different types of exams - quizzes, in-class activity etc. Based on custom types is who is the exam applicable to - Ex: Re-exam is only for people who missed the Exam.

9. In `Attendance`,
    - Easy marking of attendance with optional notes/remarks.

Does this mean that students mark their attendance on IMS? Who or what exactly is marking the attendance? What are the optional notes/remarks?
[DM] No, administration marks the attendance(unless you are able to desgin a system _which logically makes sense_ i.e where students can marks own attendace without the risk of faking it). Optional notes/remarks is allows to put addtional notes/remarks while attendacne is marked. Ex: Attendace wasn't normally marked but then marked later by Profs mail would have a remark per se.

10. In `Finance`,
    - Comprehensive fee classification and separate fee collection date design.
    1. What does "comprehensive fee classification" mean? How are we classifiying the fees?
    2. What does "separate fee collection date design" mean? What are we separating exactly?

[DM] 1. Yes, across branches. 2. Different payment modes.

11. In `Finance`,
    - Automatic transaction facilities and integration with the payslip system.

What does "Automatic transaction facilities" mean? What is a "manual transaction facility" that we are trying to avoid?
[DM] Yes. No need for finanance depratment to manually verify all the payment modes (as is in some of the current payment methods).

12. In `Finance`,
    - Assign specific tutors to batches for financial tracking.

Are we suppose to assign specific faculty to deal with the fee collection of different batches like CSE, ECE etc.?
[DM] Yes. Facutly/administration.

13. In `Messages`,
    - Broadcast information regarding school events, news, and holidays.

Isn't this a part of news? If not, what would news have?
[DM] In implementation, you either have to implement news or messages - can't do both. The feautre of broadcasting it is common to both. But the differences being enabling direct communication - that is not specific groups/selected individuals, and one way vs two way communication.

14. In `Time Table`,
    - Drag-and-drop creation interface for easy scheduling.

Time table is not a file upload portal right? What are we dragging and dropping here?
[DM] The couses being assigned to a slot.

15. In `Dashboard` what does "innovative" search bar mean. What exactly are we supposed to implement in that?
[DM]
>Innovative ‘Search bar’ enables any layman user to use the system in seconds of login

Enabling the search throughout the system to reach any point of the system directly via tha search bar.

16. Who is the user of the system? Do we need to make it from the perspective of serving an administrator?
[DM] Both.

17. Does our UI need to have placeholders for the features we aren't implementing fully or is it okay to leave them out?
[DM]It needs to have plceholders if you want to show thier presence without implementing them (majorly for the dashboard, for the other 2 modules to be implemented, you need to implement the entire module - can use stubs for other modules they interact with)

18. Since we are designing an end-user software, and not a library/api, how should the documentation be?
[DM] No unnecessary verbosity, but enough to understand the entire system and codebase for it. Documentation is still needed even if it is an end-toend user software and not library/api as developers would be working on it.

19. In `Human Resources`, can the exact specifications be given for 
• Robust payroll management with authenticated one-click payslip approval/rejection.
• Efficient leave management system and advanced employee search.

[DM]
- Easy payslip approval for various types of employees for approval/rejection
- For employees going on leaves(applying and approving) and looking up thier records for various detials must be possible in an easy manner - feel free to design the system as to how would you enable this.

20. In `Dashboard`, what does it mean by graduation facilities?
[DM] Alumni facilities - allowing them to login and allow a subset of features (make logical assumptions).

21. Category B: Data Management Modules (Choose ONE):
These modules focus primarily on data entry, retrieval, and user interactions.
– Student Admission
– Attendance
– Student Details or Manage Users
– Messages or Manage News
Are we supposed to implement any one out of 6 or any one out of 4 (like both student details AND manage users)?
[DM] Any one out of 6 (students OR manage users).

22. Please specify who are the users for the modules given.
[DM] You should know the answer to that.

23. For the attendance, will the administrator mark attendance student by student for each course manually, or can we assume that he will upload a CSV of attendance data (with manual override ofcourse)? Marking attendance one by one manually is inefficient for large batches and so many courses. 
[DM] You are free to design how you want the attendance to be taken as long as it makes logical sense. Albeit, if you are going the csv route you'll need to show how and where is the csv coming from intgrated into your system.

24. Create exams based on grades, marks, or custom types; group exams as needed.
Can you please elaborate on the exams functionality a little bit? Because current IMS doesn't have that option, right? It just has a final grade for each course. It doesn't have multiple components for each course. So, is the exams component equivalent to having a grade for each course and CG calculations and stuff? Or is it that faculty should be able to create multiple evaluation components for each course like moodle? If it is the latter, will they need to input questions or just marks? Because what is the point of inputting questions.
[DM] They'll input questions and answers both for any kind of exam (who the exam is applicable to is by group). Or they could directly input marks/grades. Current IMS doesn't support it. Refer Q8.

25. The users of the system will be administrators, faculties, and students of a particular institute? or is this system supposed to be a product that an institute will then buy and configure for their own institute?
[DM] The former.

26. Should the UML class diagram fit within one page or are multiple pages allowed?
[AVN] You can use multiple pages

27. in `DASHBOARD` should our system support multiple languages. Is English and Hindi sufficient? or do we have to implement more languages? or can we leave it as a stub?
[AVN] You can choose not to implement the different languages itself, but have a mechanism that allows you to switch.

28. In timetable module, it should be admin controlled or user(student/ employee) controlled? 
[AVN] Both. Admin should be able to create the timetable, and students be able to play around with it as in alerts, add some items if they want to

29. There are 12 modules in the table, but only 11 in detailed capabilities(courses and batches is missing) 
[AVN] Ignore the Ones which are not mentioned in detail

30. In the requirements for the mandatory Dashboard, there are several functionalities that overlap with some of the modules’ requirements. To what extent do we have to implement the functionalities, if those modules aren’t the ones we select?
For example:
"Manage student categories and graduation facilities." is similar to the requirements for Student Details.
[AVN] You just need to create classes and function header from your UML diagram. You can choose not to implement the code. 

31. What are all the features entailed in the Dashboard requirement: "General settings: grading systems, automatic unique IDs, etc?" Can the grading system be elaborated on? And what other features are expected which aren't specified by etc here?
[AVN] You can look at the original IMS and decide what u want the etc to be. Grading System can be anyhing you like. Upto your creativity.

32. What is a "Responsibilities Table"? Can't find any resources on the same as an official concept, please link the same if it exists. What columns do you expect for this component?
[AVN] It simply means that you create a table with two columns, one saying who is doing what. For example left column can have 'StudentDetails' and right column can have 'stores student's information'

33. Can the UML diagram be digitally hand-drawn? Considering it will be pretty big, and a mess to draw on paper.
[AVN] Please stick with pen-paper. It has to be fully hand-drawn

34. Is the responsibilities table supposed to be hand written? Does it need to fit one one page?
[AVN] You can type it, but 'Verbosity and unnecessary verbiage will be strictly penalized'

35. In `Finance`,
    - Comprehensive fee classification and separate fee collection date design.

    What does "separate fee collection date design" mean? In q10, you said that it means "Different payment modes." Why is there 'date' then? What notion of time does "Different payment modes" have?
[AVN] Upto your creativity, look at fee collection system of IMS. Let's say for example you have UG payment on 10th May and PG payment on 12th May.
    
36. 13. In `Messages`,
    - Broadcast information regarding school events, news, and holidays.

    This is q13. You have answered it as: 
    "[DM] In implementation, you either have to implement news or messages - can't do both. The feautre of broadcasting it is common to both. But the differences being enabling direct communication - that is not specific groups/selected indivoduals, and one way vs two wy communication."
    I am talking about the modelling part, not implementation.
    1. Please clarify what 
    "But the differences being enabling direct communication - that is not specific groups/selected indivoduals, ..." means.
    Which one is not catering to specifc groups/selected individuals? Do you mean to say that news is always broadcast to everyone, and messages can be sent to 'specific groups/selected individuals'?
    2. Both News and Messages allow two-way communication, since users can comment on news, and people can message back to the sender. So what two-way communication exactly are you talking about?
[DM] Modelling is for the whole system as is. News is public - that is not catering to specific groups/individuals. By two way for messages what I meant is not public, apologies for confusion. News comments are public, while messaging could be one-on-one private/in-group.

37. In `Time Table`,
    - Drag-and-drop creation interface for easy scheduling.
    
    Drag-and-drop seems like an implementation detail right (perhaps for the implementation phase specifically)? Do we need to include the method of doing something in our UML class diagram? For example, if a user has to apply for leaves, they we can implement it as a calendar selection of leave dates, or just entering leave dates in text format, but in our class diagram these details would be abstracted out right?
[AVN] Have the function names and variables also
    
38. Relating back to q37: the search bar itself in the dashboard has a bunch of actions it can do (search for users, instant nav...), but it is a UI implementation detail.
[AVN] These functionalities will also have functions, you should include them in your UML.

39. Who can broadcast messages to everyone? Who can broadcast news? Do we need to assign it by role, or is it open for all?
[AVN] Uptoo your creativity

40. In `Messages`,
    - Inbuilt messaging system for administration, teachers, students, and parents.
    
    Ims has parent login also? Is it not limited to campus and students?

[AVN] Assume it's a new requirement.
    
41. In `Dashboard`,
    - Manage courses, batches, subjects (including electives), and batch transfers.
    
    What does batch transfer mean? Isn't that not possible?
[AVN] If someone fails, then you might want to change the batcj
    
42. For Task 1, can the design report be typed as long as its self written?
[AVN] Only UML Class Diagram needs to be hand drawn

43. Should the figma designs be made only for android (mobile view) aspect ratio or for all aspect ratios?
[AVN] Mostly for Mobile View, but cherry points for doing it for others also
    
44. In the admission forms in `Admission` and `Human Resources` who exactly is making the form and who exactly is filling the form
[AVN] Think from a user's POV. Hint: Acads office and Admin staff make those forms.

45. In `Time Table`, can we assume that each time slot is of 85 minutes only? Or do we have to account for tuts and labs too?
[AVN] Yep, that is upto you, but it would much better to keep the flexibity. 

46. Are we supposed to make the `Dashboard` for both Admin and Student view?
What if both of our modules are for admin only? Do we still make a student's view?
[AVN] Both, dashboard is common to everyone

47.  should we do task-1 only for the 2 modules we chose, or for the whole overall ims? what does the whole ims mean here, like all the modules.
[AVN] Overall, all modules, should be implemented. 

48.  Do we need to commit all steps in Task 2 separately? (Wireframe, High fidelity and final design)
[AVN] yes

49.  Can we use xml files for frontend instead of jetpack compose? Reason: Jetpack compose is Kotlin only. I'd like to do in Java.
[DM] Sure.