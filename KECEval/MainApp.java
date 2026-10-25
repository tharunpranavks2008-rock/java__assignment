import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * MainApp - Menu-Driven Command Line Interface for Kongu Engineering College (R2024)
 * Student Academic & Continuous Evaluation System using Java JDBC.
 */
public class MainApp {

    private static final StudentDAO studentDAO = new StudentDAO();
    private static final AcademicDAO academicDAO = new AcademicDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Support automated demo runner flag for testing and screen capture
        if (args.length > 0 && ("--demo".equalsIgnoreCase(args[0]) || "--showcase".equalsIgnoreCase(args[0]))) {
            runAutomatedDemonstration();
            return;
        }

        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = promptString("Enter your choice: ");
            System.out.println();
            switch (choice.trim()) {
                case "1":
                    handleStudentMenu();
                    break;
                case "2":
                    handleCourseMenu();
                    break;
                case "3":
                    handleEnrollmentMenu();
                    break;
                case "4":
                    handleIncrementalMarksMenu();
                    break;
                case "5":
                    handleReportsMenu();
                    break;
                case "6":
                    handleUtilitiesMenu();
                    break;
                case "7":
                    runAutomatedDemonstration();
                    break;
                case "0":
                    System.out.println("Exiting Kongu Engineering College Evaluation System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("[!] Invalid choice. Please select from the menu options.");
            }
            if (running) {
                System.out.println("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    // =========================================================================
    // UI HEADERS & MENUS
    // =========================================================================

    private static void printBanner() {
        System.out.println("========================================================================================");
        System.out.println("           KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024");
        System.out.println("             STUDENT ACADEMIC & CONTINUOUS EVALUATION SYSTEM (JDBC)");
        System.out.println("========================================================================================");
    }

    private static void printMainMenu() {
        System.out.println("\n================================= MAIN MENU ==================================");
        System.out.println(" [1] Student Management (Register, View, Search)");
        System.out.println(" [2] Course Catalog (Add Course, View Catalog)");
        System.out.println(" [3] Course Enrollment & Attendance Tracking (Enroll, Record Attendance)");
        System.out.println(" [4] Incremental Marks Entry (\"Then and Now\" - Phase 1, 2, 3)");
        System.out.println(" [5] Reports & Academic Analytics (Mark Sheet, Defaulters, Summary)");
        System.out.println(" [6] Database Utilities (Test Connection, Re-seed Sample Data)");
        System.out.println(" [7] Run Automated Step-by-Step Demonstration (End-to-End Showcase)");
        System.out.println(" [0] Exit Application");
        System.out.println("==============================================================================");
    }

    // =========================================================================
    // 1. STUDENT MANAGEMENT
    // =========================================================================

    private static void handleStudentMenu() {
        System.out.println("--- STUDENT MANAGEMENT ---");
        System.out.println("1. Register New Student");
        System.out.println("2. View All Registered Students");
        System.out.println("3. Search Student by Roll Number");
        String ch = promptString("Select option (1-3): ");

        switch (ch) {
            case "1":
                registerStudentInteractive();
                break;
            case "2":
                viewAllStudents();
                break;
            case "3":
                searchStudent();
                break;
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private static void registerStudentInteractive() {
        System.out.println("\n--- [Student Registration] ---");
        String roll = promptString("Enter Roll Number (e.g. 24ITR010): ");
        String name = promptString("Enter Student Name: ");
        String dept = promptString("Enter Department (e.g. IT, CSE, ECE, MECH): ");
        int sem = promptInt("Enter Current Semester (1-8): ");

        try {
            Student existing = studentDAO.getStudent(roll);
            if (existing != null) {
                System.out.println("[!] Error: Student with Roll Number " + roll + " already exists!");
                return;
            }
            Student s = new Student(roll, name, dept, sem);
            boolean ok = studentDAO.addStudent(s);
            if (ok) {
                System.out.println("[SUCCESS] Student successfully registered: " + s);
            } else {
                System.out.println("[!] Failed to register student.");
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    private static void viewAllStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            System.out.println("\n--- [Registered Students Catalog] --- (" + students.size() + " Students)");
            System.out.printf("%-12s | %-25s | %-8s | %-8s\n", "Roll No", "Student Name", "Dept", "Semester");
            System.out.println("-------------------------------------------------------------------");
            for (Student s : students) {
                System.out.printf("%-12s | %-25s | %-8s | %-8d\n",
                        s.getRollNumber(), s.getName(), s.getDepartment(), s.getCurrentSemester());
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    private static void searchStudent() {
        String roll = promptString("Enter Roll Number to Search: ");
        try {
            Student s = studentDAO.getStudent(roll);
            if (s != null) {
                System.out.println("\n[FOUND] Student Details:");
                System.out.println("  Roll Number     : " + s.getRollNumber());
                System.out.println("  Name            : " + s.getName());
                System.out.println("  Department      : " + s.getDepartment());
                System.out.println("  Current Semester: " + s.getCurrentSemester());
            } else {
                System.out.println("[!] No student found with Roll Number: " + roll);
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    // =========================================================================
    // 2. COURSE CATALOG
    // =========================================================================

    private static void handleCourseMenu() {
        System.out.println("--- COURSE CATALOG ---");
        System.out.println("1. Add New Course");
        System.out.println("2. View All Courses");
        String ch = promptString("Select option (1-2): ");

        switch (ch) {
            case "1":
                addCourseInteractive();
                break;
            case "2":
                viewAllCourses();
                break;
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private static void addCourseInteractive() {
        System.out.println("\n--- [Add New Course] ---");
        String code = promptString("Enter Course Code (e.g. 24ITT35): ");
        String title = promptString("Enter Course Title: ");
        double credits = promptDouble("Enter Credits (e.g. 3.0, 4.0, 1.5): ");
        System.out.println("Select Course Type:");
        System.out.println("  1. THEORY    (CIA: 40% | ESE: 60% | Min ESE: 45%)");
        System.out.println("  2. PRACTICAL (CIA: 60% | ESE: 40% | Min ESE: 50%)");
        System.out.println("  3. EMBEDDED  (CIA: 50% | ESE: 50% | Min ESE: 45%)");
        String typeChoice = promptString("Choice (1-3): ");
        CourseType type;
        if ("2".equals(typeChoice)) type = CourseType.PRACTICAL;
        else if ("3".equals(typeChoice)) type = CourseType.EMBEDDED;
        else type = CourseType.THEORY;

        int sem = promptInt("Enter Semester (1-8): ");

        try {
            Course existing = studentDAO.getCourse(code);
            if (existing != null) {
                System.out.println("[!] Error: Course Code " + code + " already exists!");
                return;
            }
            Course c = new Course(code, title, credits, type, sem);
            boolean ok = studentDAO.addCourse(c);
            if (ok) {
                System.out.println("[SUCCESS] Course successfully added: " + c);
            } else {
                System.out.println("[!] Failed to add course.");
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    private static void viewAllCourses() {
        try {
            List<Course> courses = studentDAO.getAllCourses();
            System.out.println("\n--- [KEC R2024 Course Catalog] --- (" + courses.size() + " Courses)");
            System.out.printf("%-10s | %-38s | %-7s | %-10s | %-4s\n",
                    "Code", "Course Title", "Credits", "Type", "Sem");
            System.out.println("-----------------------------------------------------------------------------");
            for (Course c : courses) {
                System.out.printf("%-10s | %-38s | %-7.1f | %-10s | %-4d\n",
                        c.getCourseCode(), c.getTitle(), c.getCredits(), c.getCourseType(), c.getSemester());
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    // =========================================================================
    // 3. ENROLLMENT & ATTENDANCE TRACKING
    // =========================================================================

    private static void handleEnrollmentMenu() {
        System.out.println("--- COURSE ENROLLMENT & ATTENDANCE TRACKING ---");
        System.out.println("1. Enroll Student in Course");
        System.out.println("2. Update Attendance & Medical Condonation");
        System.out.println("3. View Enrolled Courses by Student");
        String ch = promptString("Select option (1-3): ");

        switch (ch) {
            case "1":
                enrollStudentInteractive();
                break;
            case "2":
                updateAttendanceInteractive();
                break;
            case "3":
                viewStudentEnrollments();
                break;
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private static void enrollStudentInteractive() {
        System.out.println("\n--- [Enroll Student in Course] ---");
        String roll = promptString("Enter Student Roll Number: ");
        String code = promptString("Enter Course Code: ");
        int sem = promptInt("Enter Semester: ");
        int held = promptInt("Enter Initial Classes Held (e.g. 60): ");
        int attended = promptInt("Enter Initial Classes Attended (e.g. 55): ");
        String condonedStr = promptString("Medical Condonation Approved? (Y/N, default N): ");
        boolean condoned = "Y".equalsIgnoreCase(condonedStr);

        try {
            if (studentDAO.getStudent(roll) == null) {
                System.out.println("[!] Error: Student not found with Roll Number: " + roll);
                return;
            }
            if (studentDAO.getCourse(code) == null) {
                System.out.println("[!] Error: Course not found with Code: " + code);
                return;
            }
            if (attended > held) {
                System.out.println("[!] Error: Classes attended cannot exceed classes held.");
                return;
            }

            int enrollId = studentDAO.enrollStudent(roll, code, sem, held, attended, condoned);
            if (enrollId > 0) {
                double pct = held > 0 ? (attended * 100.0) / held : 100.0;
                System.out.printf("[SUCCESS] Student %s enrolled into %s! (Enrollment ID: %d, Attendance: %.1f%%)\n",
                        roll, code, enrollId, pct);
            } else {
                System.out.println("[!] Enrollment failed.");
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    private static void updateAttendanceInteractive() {
        System.out.println("\n--- [Update Attendance & Medical Condonation] ---");
        String roll = promptString("Enter Student Roll Number: ");
        String code = promptString("Enter Course Code: ");
        int sem = promptInt("Enter Semester: ");

        try {
            Enrollment en = studentDAO.getEnrollment(roll, code, sem);
            if (en == null) {
                System.out.println("[!] No enrollment found for " + roll + " in course " + code);
                return;
            }

            System.out.println("Current Record: Attended " + en.getClassesAttended() + "/" + en.getClassesHeld() +
                    " (" + String.format("%.1f", en.getAttendancePercentage()) + "%) | Condoned: " + en.isMedicalCondonation());

            int held = promptInt("Enter New Total Classes Held: ");
            int attended = promptInt("Enter New Total Classes Attended: ");
            String condStr = promptString("Medical Condonation Approved? (Y/N): ");
            boolean condoned = "Y".equalsIgnoreCase(condStr);

            if (attended > held) {
                System.out.println("[!] Error: Classes attended cannot exceed classes held.");
                return;
            }

            boolean ok = studentDAO.updateAttendance(en.getEnrollmentId(), held, attended, condoned);
            if (ok) {
                Enrollment updated = studentDAO.getEnrollmentById(en.getEnrollmentId());
                System.out.printf("[SUCCESS] Attendance updated! Attendance: %.1f%% | Status: %s\n",
                        updated.getAttendancePercentage(), updated.getAttendanceStatusText());
            } else {
                System.out.println("[!] Failed to update attendance.");
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    private static void viewStudentEnrollments() {
        String roll = promptString("Enter Student Roll Number: ");
        int sem = promptInt("Enter Semester: ");
        try {
            List<Enrollment> enrollments = studentDAO.getEnrollmentsByStudent(roll, sem);
            if (enrollments.isEmpty()) {
                System.out.println("[!] No enrollments found for student " + roll + " in semester " + sem);
                return;
            }

            System.out.println("\n--- [Enrollment & Attendance for " + roll + " - Sem " + sem + "] ---");
            System.out.printf("%-5s | %-10s | %-32s | %-9s | %-10s | %-8s | %-15s\n",
                    "ID", "Course", "Title", "Type", "Attendance", "Percent", "Eligibility");
            System.out.println("----------------------------------------------------------------------------------------------------");
            for (Enrollment en : enrollments) {
                System.out.printf("%-5d | %-10s | %-32s | %-9s | %3d / %-4d | %5.1f%%  | %-15s\n",
                        en.getEnrollmentId(), en.getCourseCode(), en.getCourseTitle(),
                        en.getCourseType(), en.getClassesAttended(), en.getClassesHeld(),
                        en.getAttendancePercentage(),
                        en.isEligibleForESE() ? "ELIGIBLE" : "BARRED (SA)");
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    // =========================================================================
    // 4. INCREMENTAL MARKS ENTRY ("THEN AND NOW" LIFECYCLE)
    // =========================================================================

    private static void handleIncrementalMarksMenu() {
        System.out.println("--- INCREMENTAL MARKS ENTRY (KEC R2024 LIFECYCLE) ---");
        System.out.println("1. Phase 1 (Mid-Semester)    : Record CIA Test 1 & CIA Test 2 (0-100)");
        System.out.println("2. Phase 2 (End of Semester) : Record Assignments / Quizzes / Lab Evaluations (0-100)");
        System.out.println("3. Phase 3 (Post Semester)   : Record End Semester Exam (ESE) Marks (0-100) or Mark AB");
        System.out.println("4. View Current Assessment Marks for an Enrollment");
        String ch = promptString("Select option (1-4): ");

        switch (ch) {
            case "1":
                recordPhase1Interactive();
                break;
            case "2":
                recordPhase2Interactive();
                break;
            case "3":
                recordPhase3Interactive();
                break;
            case "4":
                viewAssessmentMarksInteractive();
                break;
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    private static void recordPhase1Interactive() {
        System.out.println("\n--- [Phase 1: Mid-Semester Marks Entry (CIA 1 & CIA 2)] ---");
        String roll = promptString("Enter Student Roll Number: ");
        String code = promptString("Enter Course Code: ");
        int sem = promptInt("Enter Semester: ");

        try {
            Enrollment en = studentDAO.getEnrollment(roll, code, sem);
            if (en == null) {
                System.out.println("[!] Enrollment not found for " + roll + " in " + code);
                return;
            }

            System.out.printf("Selected: Student %s | Course: %s (%s)\n", roll, code, en.getCourseTitle());
            double cia1 = promptDouble("Enter CIA Test 1 Marks (0.0 - 100.0): ");
            double cia2 = promptDouble("Enter CIA Test 2 Marks (0.0 - 100.0): ");

            boolean ok = academicDAO.recordPhase1Marks(en.getEnrollmentId(), cia1, cia2);
            if (ok) {
                System.out.printf("[SUCCESS] Phase 1 Recorded: CIA 1 = %.1f, CIA 2 = %.1f (Tests Avg: %.1f)\n",
                        cia1, cia2, (cia1 + cia2) / 2.0);
            } else {
                System.out.println("[!] Failed to record Phase 1 marks.");
            }
        } catch (IllegalArgumentException | SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private static void recordPhase2Interactive() {
        System.out.println("\n--- [Phase 2: End-of-Semester CIA (Assignments / Quizzes / Lab)] ---");
        String roll = promptString("Enter Student Roll Number: ");
        String code = promptString("Enter Course Code: ");
        int sem = promptInt("Enter Semester: ");

        try {
            Enrollment en = studentDAO.getEnrollment(roll, code, sem);
            if (en == null) {
                System.out.println("[!] Enrollment not found for " + roll + " in " + code);
                return;
            }

            System.out.printf("Selected: Student %s | Course: %s (%s)\n", roll, code, en.getCourseTitle());
            double asgn = promptDouble("Enter Assignment / Quiz / Lab Evaluation Marks (0.0 - 100.0): ");

            boolean ok = academicDAO.recordPhase2Marks(en.getEnrollmentId(), asgn);
            if (ok) {
                AssessmentMarks marks = academicDAO.getAssessmentMarks(en.getEnrollmentId());
                Double ciaTotal = marks != null ? marks.calculateCiaTotal100() : null;
                System.out.printf("[SUCCESS] Phase 2 Recorded: Assignment/Lab = %.1f\n", asgn);
                if (ciaTotal != null) {
                    System.out.printf("--> CIA Total (100 Scale) is now: %.2f / 100.0\n", ciaTotal);
                }
            } else {
                System.out.println("[!] Failed to record Phase 2 marks.");
            }
        } catch (IllegalArgumentException | SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private static void recordPhase3Interactive() {
        System.out.println("\n--- [Phase 3: Post-Semester End Semester Examination (ESE)] ---");
        String roll = promptString("Enter Student Roll Number: ");
        String code = promptString("Enter Course Code: ");
        int sem = promptInt("Enter Semester: ");

        try {
            Enrollment en = studentDAO.getEnrollment(roll, code, sem);
            if (en == null) {
                System.out.println("[!] Enrollment not found for " + roll + " in " + code);
                return;
            }

            System.out.printf("Selected: Student %s | Course: %s (%s) | Type: %s\n",
                    roll, code, en.getCourseTitle(), en.getCourseType());
            System.out.printf("Attendance: %d/%d (%.1f%%) | Status: %s\n",
                    en.getClassesAttended(), en.getClassesHeld(), en.getAttendancePercentage(),
                    en.getAttendanceStatusText());

            // Attendance Check Enforced before entry
            if (!en.isEligibleForESE()) {
                System.out.println("\n[!] ATTENDANCE ELIGIBILITY VIOLATION:");
                System.out.printf("    Student has %.1f%% attendance and is NOT eligible for ESE.\n", en.getAttendancePercentage());
                System.out.println("    As per KEC Regulation 2024, the student is barred from ESE and awarded grade SA.");
                return;
            }

            String isAbs = promptString("Was student Absent in ESE? (Y/N, default N): ");
            boolean absent = "Y".equalsIgnoreCase(isAbs);

            Double eseMarks = null;
            if (!absent) {
                eseMarks = promptDouble("Enter ESE Marks (0.0 - 100.0): ");
            }

            boolean ok = academicDAO.recordPhase3Marks(en.getEnrollmentId(), eseMarks, absent);
            if (ok) {
                if (absent) {
                    System.out.println("[SUCCESS] Student marked ABSENT in ESE (Grade: AB, GP: 0).");
                } else {
                    CourseEvaluationResult eval = academicDAO.evaluateCourse(en.getEnrollmentId());
                    System.out.printf("[SUCCESS] ESE Marks Recorded: %.1f\n", eseMarks);
                    System.out.printf("--> Total Score: %.2f | Grade: %s | Grade Point: %d | Status: %s\n",
                            eval.getTotalMarks(), eval.getLetterGrade(), eval.getGradePoint(), eval.getResultStatus());
                }
            } else {
                System.out.println("[!] Failed to record Phase 3 marks.");
            }
        } catch (IllegalStateException | IllegalArgumentException | SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private static void viewAssessmentMarksInteractive() {
        String roll = promptString("Enter Student Roll Number: ");
        String code = promptString("Enter Course Code: ");
        int sem = promptInt("Enter Semester: ");

        try {
            Enrollment en = studentDAO.getEnrollment(roll, code, sem);
            if (en == null) {
                System.out.println("[!] Enrollment not found.");
                return;
            }

            CourseEvaluationResult eval = academicDAO.evaluateCourse(en.getEnrollmentId());
            AssessmentMarks m = eval.getMarks();

            System.out.println("\n==================== ASSESSMENT STATUS ====================");
            System.out.println("Student      : " + en.getRollNumber() + " (" + en.getStudentName() + ")");
            System.out.println("Course       : " + en.getCourseCode() + " - " + en.getCourseTitle());
            System.out.println("Type         : " + en.getCourseType() + " | Credits: " + en.getCredits());
            System.out.println("Attendance   : " + en.getClassesAttended() + "/" + en.getClassesHeld() +
                    " (" + String.format("%.1f", eval.getAttendancePct()) + "%) -> " + eval.getAttendanceStatus());
            System.out.println("-----------------------------------------------------------");
            System.out.println("Phase 1 (CIA 1) : " + (m.getCia1Marks() != null ? String.format("%.1f", m.getCia1Marks()) : "PENDING"));
            System.out.println("Phase 1 (CIA 2) : " + (m.getCia2Marks() != null ? String.format("%.1f", m.getCia2Marks()) : "PENDING"));
            System.out.println("Phase 2 (Asgn)  : " + (m.getAssignmentMarks() != null ? String.format("%.1f", m.getAssignmentMarks()) : "PENDING"));
            System.out.println("CIA Total (100) : " + (eval.getCiaTotal100() != null ? String.format("%.2f", eval.getCiaTotal100()) : "PENDING"));
            System.out.println("Scaled CIA      : " + (eval.getScaledCia() != null ? String.format("%.2f", eval.getScaledCia()) : "PENDING"));
            System.out.println("-----------------------------------------------------------");
            System.out.println("Phase 3 (ESE)   : " + (m.isAbsentEse() ? "ABSENT (AB)" : (m.getEseMarks() != null ? String.format("%.1f", m.getEseMarks()) : "PENDING")));
            System.out.println("Scaled ESE      : " + (eval.getScaledEse() != null ? String.format("%.2f", eval.getScaledEse()) : "PENDING"));
            System.out.println("Total Marks     : " + (eval.getTotalMarks() != null ? String.format("%.2f", eval.getTotalMarks()) : "PENDING"));
            System.out.println("Letter Grade    : " + eval.getLetterGrade());
            System.out.println("Grade Point     : " + eval.getGradePoint());
            System.out.println("Evaluation Stat : " + eval.getResultStatus());
            System.out.println("===========================================================");
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    // =========================================================================
    // 5. REPORTS & ACADEMIC ANALYTICS
    // =========================================================================

    private static void handleReportsMenu() {
        System.out.println("--- REPORTS & ACADEMIC ANALYTICS ---");
        System.out.println("1. Student Semester Mark Sheet (Formal Grade Card with SGPA & %)");
        System.out.println("2. Attendance Defaulter Report (Attendance < 65% flagged SA)");
        System.out.println("3. Class Performance & Department Result Summary");
        String ch = promptString("Select option (1-3): ");

        switch (ch) {
            case "1":
                generateMarkSheetInteractive();
                break;
            case "2":
                generateDefaulterReportInteractive();
                break;
            case "3":
                generateClassPerformanceInteractive();
                break;
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    public static void printFormalMarkSheet(StudentMarkSheet sheet) {
        Student st = sheet.getStudent();
        System.out.println("========================================================================================================================");
        System.out.println("                              KONGU ENGINEERING COLLEGE (AUTONOMOUS), PERUNDURAI");
        System.out.println("                                  STUDENT SEMESTER GRADE SHEET - REGULATION 2024");
        System.out.println("========================================================================================================================");
        System.out.printf("  Roll Number : %-20s  Student Name : %-30s\n", st.getRollNumber(), st.getName());
        System.out.printf("  Department  : %-20s  Semester     : %d\n", st.getDepartment(), sheet.getSemester());
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-9s | %-32s | %-9s | %-4s | %-6s | %-5s | %-5s | %-5s | %-6s | %-5s | %-5s | %-5s | %-4s | %-2s | %-12s\n",
                "Code", "Course Title", "Type", "Cred", "Attd%", "CIA1", "CIA2", "Asgn", "CIA-Sc", "ESE", "ESE-Sc", "Total", "Grd", "GP", "Status");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        for (CourseEvaluationResult cr : sheet.getCourseResults()) {
            Course c = cr.getCourse();
            AssessmentMarks m = cr.getMarks();
            String cia1 = m.getCia1Marks() != null ? String.format("%.0f", m.getCia1Marks()) : "-";
            String cia2 = m.getCia2Marks() != null ? String.format("%.0f", m.getCia2Marks()) : "-";
            String asgn = m.getAssignmentMarks() != null ? String.format("%.0f", m.getAssignmentMarks()) : "-";
            String ciaSc = cr.getScaledCia() != null ? String.format("%.1f", cr.getScaledCia()) : "-";
            String eseRaw = m.isAbsentEse() ? "AB" : (m.getEseMarks() != null ? String.format("%.0f", m.getEseMarks()) : "-");
            String eseSc = cr.getScaledEse() != null ? String.format("%.1f", cr.getScaledEse()) : "-";
            String total = cr.getTotalMarks() != null ? String.format("%.1f", cr.getTotalMarks()) : "-";

            System.out.printf("%-9s | %-32s | %-9s | %4.1f | %5.1f%% | %5s | %5s | %5s | %6s | %5s | %6s | %5s | %-4s | %2d | %-12s\n",
                    c.getCourseCode(),
                    c.getTitle().length() > 32 ? c.getTitle().substring(0, 31) : c.getTitle(),
                    c.getCourseType(),
                    c.getCredits(),
                    cr.getAttendancePct(),
                    cia1, cia2, asgn, ciaSc,
                    eseRaw, eseSc, total,
                    cr.getLetterGrade(),
                    cr.getGradePoint(),
                    cr.getResultStatus().length() > 12 ? cr.getResultStatus().substring(0, 12) : cr.getResultStatus());
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("  Total Enrolled Credits : %-6.1f              Total Credits Earned    : %-6.1f\n",
                sheet.getTotalEnrolledCredits(), sheet.getTotalEarnedCredits());
        System.out.printf("  Cumulative Grade Points: %-6.2f              Semester SGPA           : %.2f / 10.00\n",
                sheet.getTotalQualityPoints(), sheet.getSgpa());
        System.out.printf("  Equivalent Percentage  : %-6.2f%%             Overall Academic Result : %s\n",
                sheet.getEquivalentPercentage(), sheet.getStanding());
        System.out.println("========================================================================================================================");
    }

    private static void generateMarkSheetInteractive() {
        String roll = promptString("Enter Student Roll Number: ");
        int sem = promptInt("Enter Semester: ");
        try {
            StudentMarkSheet sheet = academicDAO.generateStudentMarkSheet(roll, sem);
            if (sheet == null) {
                System.out.println("[!] Student not found with Roll Number: " + roll);
                return;
            }
            if (sheet.getCourseResults().isEmpty()) {
                System.out.println("[!] No courses enrolled for student " + roll + " in semester " + sem);
                return;
            }
            printFormalMarkSheet(sheet);
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    public static void printDefaulterReport(List<CourseEvaluationResult> defaulters, int semester, String dept) {
        System.out.println("========================================================================================================");
        System.out.println("                             KONGU ENGINEERING COLLEGE (AUTONOMOUS)");
        System.out.println("                         ATTENDANCE DEFAULTER REPORT (BARRED - GRADE: SA)");
        System.out.printf("                                  SEMESTER: %d | DEPARTMENT: %s\n",
                semester, (dept == null || dept.isEmpty() ? "ALL DEPARTMENTS" : dept.toUpperCase()));
        System.out.println("========================================================================================================");
        System.out.printf("%-10s | %-22s | %-6s | %-9s | %-12s | %-8s | %-10s | %-12s\n",
                "Roll No", "Student Name", "Dept", "Course", "Held / Attd", "Attd %", "Condoned", "Awarded Grade");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        if (defaulters.isEmpty()) {
            System.out.println("                 [NO ATTENDANCE DEFAULTERS FOUND - ALL STUDENTS ARE ELIGIBLE]");
        } else {
            for (CourseEvaluationResult cr : defaulters) {
                Enrollment en = cr.getEnrollment();
                System.out.printf("%-10s | %-22s | %-6s | %-9s | %4d / %-6d | %6.1f%% | %-10s | %-12s\n",
                        en.getRollNumber(),
                        en.getStudentName(),
                        en.getRollNumber().substring(2, 4), // Approximate or derived dept
                        en.getCourseCode(),
                        en.getClassesHeld(),
                        en.getClassesAttended(),
                        cr.getAttendancePct(),
                        en.isMedicalCondonation() ? "YES" : "NO",
                        "SA (BARRED)");
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.printf("  Total Defaulter Cases Flagged: %d  |  Policy: Shortage <65%% or Unapproved Condonation = SA Grade\n",
                defaulters.size());
        System.out.println("========================================================================================================");
    }

    private static void generateDefaulterReportInteractive() {
        int sem = promptInt("Enter Semester: ");
        String dept = promptString("Enter Department (Leave blank for ALL): ");
        try {
            List<CourseEvaluationResult> list = academicDAO.getAttendanceDefaulters(sem, dept);
            printDefaulterReport(list, sem, dept);
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void printClassPerformanceSummary(Map<String, Object> summary) {
        String dept = (String) summary.get("department");
        int sem = (int) summary.get("semester");
        int total = (int) summary.get("totalStudents");
        int pass = (int) summary.get("passCount");
        int arrear = (int) summary.get("arrearCount");
        int sa = (int) summary.get("saCount");
        double passPct = (double) summary.get("passPercentage");
        double avgSgpa = (double) summary.get("classAvgSgpa");
        Map<String, Integer> dist = (Map<String, Integer>) summary.get("gradeDistribution");
        List<StudentMarkSheet> topList = (List<StudentMarkSheet>) summary.get("topPerformers");

        System.out.println("========================================================================================");
        System.out.println("                        KONGU ENGINEERING COLLEGE (AUTONOMOUS)");
        System.out.printf("                     CLASS PERFORMANCE & RESULT SUMMARY - SEMESTER %d\n", sem);
        System.out.printf("                                DEPARTMENT: %s\n", dept);
        System.out.println("========================================================================================");
        System.out.printf("  Total Students Evaluated  : %-4d\n", total);
        System.out.printf("  Students Passed (Clear All): %-4d\n", pass);
        System.out.printf("  Students with Arrears     : %-4d  (Attendance Barred SA: %d)\n", arrear, sa);
        System.out.printf("  Department Pass Percentage: %.2f%%\n", passPct);
        System.out.printf("  Class Average SGPA        : %.2f / 10.00 (Equivalent: %.2f%%)\n", avgSgpa, avgSgpa * 10.0);
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("Grade Distribution Across All Course Assessments:");
        System.out.printf("  O  : %-3d | A+ : %-3d | A  : %-3d | B+ : %-3d | B  : %-3d | RA : %-3d | SA : %-3d | AB : %-3d\n",
                dist.getOrDefault("O", 0), dist.getOrDefault("A+", 0),
                dist.getOrDefault("A", 0), dist.getOrDefault("B+", 0),
                dist.getOrDefault("B", 0), dist.getOrDefault("RA", 0),
                dist.getOrDefault("SA", 0), dist.getOrDefault("AB", 0));
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("Academic Merit / Rank List:");
        System.out.printf("%-4s | %-12s | %-24s | %-6s | %-8s | %-15s\n",
                "Rank", "Roll Number", "Student Name", "SGPA", "Equiv %", "Result Standing");
        System.out.println("----------------------------------------------------------------------------------------");
        int rank = 1;
        for (StudentMarkSheet s : topList) {
            System.out.printf("%-4d | %-12s | %-24s | %-6.2f | %-7.2f%% | %-15s\n",
                    rank++, s.getStudent().getRollNumber(), s.getStudent().getName(),
                    s.getSgpa(), s.getEquivalentPercentage(), s.getStanding());
        }
        System.out.println("========================================================================================");
    }

    private static void generateClassPerformanceInteractive() {
        String dept = promptString("Enter Department (e.g. IT, CSE): ");
        int sem = promptInt("Enter Semester: ");
        try {
            Map<String, Object> summary = academicDAO.getClassPerformanceSummary(dept, sem);
            printClassPerformanceSummary(summary);
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }
    }

    // =========================================================================
    // 6. DATABASE UTILITIES
    // =========================================================================

    private static void handleUtilitiesMenu() {
        System.out.println("--- DATABASE UTILITIES ---");
        System.out.println("1. Test MySQL Database Connection");
        System.out.println("2. Reset Database & Re-seed Sample KEC R2024 Data");
        String ch = promptString("Select option (1-2): ");

        switch (ch) {
            case "1":
                DBConnection.testConnection();
                break;
            case "2":
                System.out.println("Re-initializing database from 'kec_schema.sql'...");
                boolean ok = academicDAO.resetDatabase("kec_schema.sql");
                if (ok) {
                    System.out.println("[SUCCESS] Database successfully re-seeded with realistic KEC R2024 academic records!");
                } else {
                    System.out.println("[!] Failed to reset database. Check console logs.");
                }
                break;
            default:
                System.out.println("[!] Invalid option.");
        }
    }

    // =========================================================================
    // 7. AUTOMATED STEP-BY-STEP DEMONSTRATION SHOWCASE
    // =========================================================================

    /**
     * Executes a complete end-to-end showcase covering all requirements:
     * 1. Register student & course
     * 2. Enroll student and track attendance
     * 3. Phased incremental marks entry (Phase 1, Phase 2, Phase 3)
     * 4. Enforce attendance eligibility (SA)
     * 5. Generate Student Semester Mark Sheet with calculated SGPA & %
     * 6. Generate Attendance Defaulter Report
     * 7. Generate Class Performance & Result Summary
     */
    public static void runAutomatedDemonstration() {
        System.out.println("\n****************************************************************************************");
        System.out.println("       KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024 DEMONSTRATION");
        System.out.println("                FULL END-TO-END AUTOMATED VERIFICATION SHOWCASE");
        System.out.println("****************************************************************************************\n");

        try {
            // STEP 1: Add a Student and a Course
            System.out.println(">>> STEP 1: Adding a New Student and Course Catalog Entry...");
            String demoRoll = "24ITR099";
            String demoCourseCode = "24ITT39";

            // Clean up demo student if exists
            Student existingStudent = studentDAO.getStudent(demoRoll);
            if (existingStudent == null) {
                Student newStudent = new Student(demoRoll, "Vigneshwaran S", "IT", 3);
                studentDAO.addStudent(newStudent);
                System.out.println("[OK] Registered New Student: " + newStudent);
            } else {
                System.out.println("[OK] Demo Student Exists: " + existingStudent);
            }

            Course existingCourse = studentDAO.getCourse(demoCourseCode);
            if (existingCourse == null) {
                Course newCourse = new Course(demoCourseCode, "Cloud Computing & DevOps", 3.0, CourseType.THEORY, 3);
                studentDAO.addCourse(newCourse);
                System.out.println("[OK] Added New Course: " + newCourse);
            } else {
                System.out.println("[OK] Demo Course Exists: " + existingCourse);
            }

            // STEP 2: Enroll Student and Record Attendance
            System.out.println("\n>>> STEP 2: Enrolling Student into Course & Tracking Attendance (54/60 = 90.0%)...");
            Enrollment demoEn = studentDAO.getEnrollment(demoRoll, demoCourseCode, 3);
            int enrollId;
            if (demoEn == null) {
                enrollId = studentDAO.enrollStudent(demoRoll, demoCourseCode, 3, 60, 54, false);
                System.out.printf("[OK] Enrolled %s in %s (Enrollment ID: %d, Attendance: 90.0%% - ELIGIBLE)\n",
                        demoRoll, demoCourseCode, enrollId);
            } else {
                enrollId = demoEn.getEnrollmentId();
                studentDAO.updateAttendance(enrollId, 60, 54, false);
                System.out.printf("[OK] Using existing enrollment #%d (Attendance: 90.0%% - ELIGIBLE)\n", enrollId);
            }

            // STEP 3: Incremental Marks Entry ("Then and Now")
            System.out.println("\n>>> STEP 3: Phased / Incremental Marks Entry (\"Then and Now\"):");
            System.out.println("  --> [Phase 1 (Mid-Semester)]: Entering CIA Test 1 = 88.0, CIA Test 2 = 92.0");
            academicDAO.recordPhase1Marks(enrollId, 88.0, 92.0);
            System.out.println("      [Phase 1 SAVED successfully]");

            System.out.println("  --> [Phase 2 (End of Semester - CIA)]: Entering Assignments / Quizzes = 94.0");
            academicDAO.recordPhase2Marks(enrollId, 94.0);
            System.out.println("      [Phase 2 SAVED successfully]");

            System.out.println("  --> [Phase 3 (Post Semester - ESE)]: Entering End Semester Exam Marks = 90.0");
            academicDAO.recordPhase3Marks(enrollId, 90.0, false);
            System.out.println("      [Phase 3 SAVED successfully]");

            // Also enroll demo student in a practical course and embedded course for a rich mark sheet
            Enrollment enLab = studentDAO.getEnrollment(demoRoll, "24ITL31", 3);
            if (enLab == null) {
                int labId = studentDAO.enrollStudent(demoRoll, "24ITL31", 3, 30, 29, false);
                academicDAO.recordPhase1Marks(labId, 92.0, 95.0);
                academicDAO.recordPhase2Marks(labId, 96.0);
                academicDAO.recordPhase3Marks(labId, 92.0, false);
            }

            Enrollment enDpsd = studentDAO.getEnrollment(demoRoll, "24ITI31", 3);
            if (enDpsd == null) {
                int dpsdId = studentDAO.enrollStudent(demoRoll, "24ITI31", 3, 60, 56, false);
                academicDAO.recordPhase1Marks(dpsdId, 85.0, 88.0);
                academicDAO.recordPhase2Marks(dpsdId, 90.0);
                academicDAO.recordPhase3Marks(dpsdId, 86.0, false);
            }

            // STEP 4: Demonstrate Attendance Filtering Rule (Barred Student SA)
            System.out.println("\n>>> STEP 4: Demonstrating Attendance Filter & Condonation Rule Enforcement:");
            System.out.println("  Attempting to enter Phase 3 ESE marks for Attendance Defaulter (24ITR004 in 24ITT31, 55% attendance)...");
            Enrollment defaulterEn = studentDAO.getEnrollment("24ITR004", "24ITT31", 3);
            if (defaulterEn != null) {
                try {
                    academicDAO.recordPhase3Marks(defaulterEn.getEnrollmentId(), 75.0, false);
                } catch (IllegalStateException e) {
                    System.out.println("  [RULE ENFORCED!] Engine blocked ESE entry: " + e.getMessage());
                }
            }

            // STEP 5: Generated Formal Semester Mark Sheet for 24ITR001
            System.out.println("\n>>> STEP 5: Formal Student Semester Mark Sheet with SGPA & Percentage (24ITR001):");
            StudentMarkSheet sheet1 = academicDAO.generateStudentMarkSheet("24ITR001", 3);
            printFormalMarkSheet(sheet1);

            // STEP 6: Generated Mark Sheet for Demo Student 24ITR099
            System.out.println("\n>>> STEP 6: Generated Mark Sheet for Newly Added Student (24ITR099):");
            StudentMarkSheet sheetDemo = academicDAO.generateStudentMarkSheet(demoRoll, 3);
            printFormalMarkSheet(sheetDemo);

            // STEP 7: Attendance Defaulter Report
            System.out.println("\n>>> STEP 7: Attendance Defaulter Report (All students <65% flagged SA):");
            List<CourseEvaluationResult> defaulters = academicDAO.getAttendanceDefaulters(3, null);
            printDefaulterReport(defaulters, 3, null);

            // STEP 8: Class Performance & Department Result Summary
            System.out.println("\n>>> STEP 8: Class Performance & Result Summary for Department of IT:");
            Map<String, Object> itSummary = academicDAO.getClassPerformanceSummary("IT", 3);
            printClassPerformanceSummary(itSummary);

            System.out.println("\n[SUCCESS] Automated Demonstration Complete! All KEC R2024 features verified.");

        } catch (SQLException e) {
            System.err.println("[SHOWCASE SQL ERROR] " + e.getMessage());
        }
    }

    // =========================================================================
    // CONSOLE INPUT HELPERS
    // =========================================================================

    private static String promptString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int promptInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("[!] Please enter a valid integer.");
            }
        }
    }

    private static double promptDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("[!] Please enter a valid decimal number.");
            }
        }
    }
}

