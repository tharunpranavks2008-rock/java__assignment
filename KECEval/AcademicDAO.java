import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AcademicDAO - Assessment Engine, Phased Marks Entry, Attendance Rules,
 * and Formal Academic Report Generation (Kongu Engineering College R2024).
 */
public class AcademicDAO {

    private final StudentDAO studentDAO = new StudentDAO();

    // =========================================================================
    // PHASED / INCREMENTAL MARKS ENTRY ("THEN AND NOW")
    // =========================================================================

    /**
     * Phase 1 (Mid-Semester): Record CIA Test 1 (0-100) and CIA Test 2 (0-100).
     */
    public boolean recordPhase1Marks(int enrollmentId, double cia1, double cia2) throws SQLException {
        if (cia1 < 0.0 || cia1 > 100.0 || cia2 < 0.0 || cia2 > 100.0) {
            throw new IllegalArgumentException("CIA test marks must be within the range [0.0 - 100.0].");
        }
        ensureAssessmentRecordExists(enrollmentId);

        String sql = "UPDATE assessment_marks SET cia1_marks = ?, cia2_marks = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, cia1);
            ps.setDouble(2, cia2);
            ps.setInt(3, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Phase 2 (End of Semester - CIA): Record Assignments / Quizzes / Lab Evaluations (0-100).
     */
    public boolean recordPhase2Marks(int enrollmentId, double assignmentMarks) throws SQLException {
        if (assignmentMarks < 0.0 || assignmentMarks > 100.0) {
            throw new IllegalArgumentException("Assignment/Lab marks must be within the range [0.0 - 100.0].");
        }
        ensureAssessmentRecordExists(enrollmentId);

        String sql = "UPDATE assessment_marks SET assignment_marks = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, assignmentMarks);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Phase 3 (Post Semester): Record End Semester Examination (ESE) Marks (0-100) or Mark Absence (AB).
     * 
     * ENFORCES KEC R2024 ATTENDANCE FILTER:
     * - Attendance >= 75%: Allowed.
     * - Attendance 65% - 74.9%: Allowed ONLY if medical condonation is approved.
     * - Attendance < 65%: Barred from ESE (Award SA grade, entry rejected).
     */
    public boolean recordPhase3Marks(int enrollmentId, Double eseMarks, boolean isAbsent) throws SQLException {
        // Enforce Attendance Filter Rule
        Enrollment enrollment = studentDAO.getEnrollmentById(enrollmentId);
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment not found for ID: " + enrollmentId);
        }

        if (!enrollment.isEligibleForESE()) {
            double pct = enrollment.getAttendancePercentage();
            String reason = pct < 65.0 
                    ? String.format("Attendance shortage (%.1f%% < 65%% threshold)", pct)
                    : String.format("Attendance conditionally deficient (%.1f%%) without Medical Condonation approval", pct);
            throw new IllegalStateException("ATTENDANCE VIOLATION: Student is BARRED from ESE. " + reason + ". Grade SA is awarded.");
        }

        if (!isAbsent && eseMarks != null) {
            if (eseMarks < 0.0 || eseMarks > 100.0) {
                throw new IllegalArgumentException("ESE marks must be within the range [0.0 - 100.0].");
            }
        }

        ensureAssessmentRecordExists(enrollmentId);

        String sql = "UPDATE assessment_marks SET ese_marks = ?, is_absent_ese = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (isAbsent || eseMarks == null) {
                ps.setNull(1, java.sql.Types.DECIMAL);
                ps.setBoolean(2, true);
            } else {
                ps.setDouble(1, eseMarks);
                ps.setBoolean(2, false);
            }
            ps.setInt(3, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Ensures an assessment_marks row exists for the given enrollment_id.
     */
    public void ensureAssessmentRecordExists(int enrollmentId) throws SQLException {
        String checkSql = "SELECT mark_id FROM assessment_marks WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    String insertSql = "INSERT INTO assessment_marks (enrollment_id) VALUES (?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, enrollmentId);
                        insertPs.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * Retrieves assessment marks for an enrollment.
     */
    public AssessmentMarks getAssessmentMarks(int enrollmentId) throws SQLException {
        String sql = "SELECT mark_id, enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese, updated_at " +
                     "FROM assessment_marks WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Double cia1 = rs.getObject("cia1_marks") != null ? rs.getDouble("cia1_marks") : null;
                    Double cia2 = rs.getObject("cia2_marks") != null ? rs.getDouble("cia2_marks") : null;
                    Double asgn = rs.getObject("assignment_marks") != null ? rs.getDouble("assignment_marks") : null;
                    Double ese = rs.getObject("ese_marks") != null ? rs.getDouble("ese_marks") : null;
                    boolean absent = rs.getBoolean("is_absent_ese");
                    return new AssessmentMarks(
                            rs.getInt("mark_id"),
                            rs.getInt("enrollment_id"),
                            cia1, cia2, asgn, ese, absent,
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        }
        return null;
    }

    // =========================================================================
    // EVALUATION ENGINE & MARK SHEET GENERATION
    // =========================================================================

    /**
     * Evaluates a single course enrollment under KEC R2024 regulations.
     */
    public CourseEvaluationResult evaluateCourse(int enrollmentId) throws SQLException {
        Enrollment enrollment = studentDAO.getEnrollmentById(enrollmentId);
        if (enrollment == null) return null;

        Course course = studentDAO.getCourse(enrollment.getCourseCode());
        if (course == null) return null;

        AssessmentMarks marks = getAssessmentMarks(enrollmentId);
        return new CourseEvaluationResult(enrollment, course, marks);
    }

    /**
     * Generates the complete formal Student Semester Mark Sheet with SGPA and Equivalent Percentage.
     */
    public StudentMarkSheet generateStudentMarkSheet(String rollNumber, int semester) throws SQLException {
        Student student = studentDAO.getStudent(rollNumber);
        if (student == null) {
            return null;
        }

        StudentMarkSheet markSheet = new StudentMarkSheet(student, semester);
        List<Enrollment> enrollments = studentDAO.getEnrollmentsByStudent(rollNumber, semester);

        for (Enrollment en : enrollments) {
            Course course = studentDAO.getCourse(en.getCourseCode());
            AssessmentMarks marks = getAssessmentMarks(en.getEnrollmentId());
            CourseEvaluationResult result = new CourseEvaluationResult(en, course, marks);
            markSheet.addCourseResult(result);
        }

        return markSheet;
    }

    /**
     * Generates the Attendance Defaulter Report:
     * Lists all students with attendance < 65% (or 65% - 74.9% without condonation) flagged with grade SA.
     */
    public List<CourseEvaluationResult> getAttendanceDefaulters(int semester, String department) throws SQLException {
        List<CourseEvaluationResult> defaulters = new ArrayList<>();
        List<Enrollment> enrollments = studentDAO.getAllEnrollments(semester);

        for (Enrollment en : enrollments) {
            Student st = studentDAO.getStudent(en.getRollNumber());
            if (department != null && !department.trim().isEmpty()) {
                if (st == null || !st.getDepartment().equalsIgnoreCase(department.trim())) {
                    continue;
                }
            }

            if (!en.isEligibleForESE()) {
                Course course = studentDAO.getCourse(en.getCourseCode());
                AssessmentMarks marks = getAssessmentMarks(en.getEnrollmentId());
                CourseEvaluationResult res = new CourseEvaluationResult(en, course, marks);
                defaulters.add(res);
            }
        }
        return defaulters;
    }

    /**
     * Computes Department-wise Class Performance and Result Summary.
     * Includes pass/fail count, class average SGPA, grade distribution, and top performers.
     */
    public Map<String, Object> getClassPerformanceSummary(String department, int semester) throws SQLException {
        Map<String, Object> summary = new LinkedHashMap<>();

        List<Student> students = studentDAO.getStudentsByDepartment(department, semester);
        int totalStudents = students.size();
        int passCount = 0;
        int arrearCount = 0;
        int saCount = 0;
        int incompleteCount = 0;

        double totalSgpaSum = 0.0;
        int sgpaEligibleCount = 0;

        Map<String, Integer> gradeDistribution = new HashMap<>();
        String[] grades = {"O", "A+", "A", "B+", "B", "RA", "SA", "AB"};
        for (String g : grades) gradeDistribution.put(g, 0);

        List<StudentMarkSheet> sheetList = new ArrayList<>();

        for (Student st : students) {
            StudentMarkSheet sheet = generateStudentMarkSheet(st.getRollNumber(), semester);
            if (sheet == null || sheet.getCourseResults().isEmpty()) {
                continue;
            }
            sheetList.add(sheet);

            if ("PASS".equals(sheet.getStanding())) {
                passCount++;
            } else if ("SHORTAGE OF ATTENDANCE (SA)".equals(sheet.getStanding())) {
                saCount++;
                arrearCount++;
            } else if ("RE-APPEARANCE (ARREAR)".equals(sheet.getStanding())) {
                arrearCount++;
            } else {
                incompleteCount++;
            }

            if (!sheet.getCourseResults().isEmpty() && sheet.getTotalEnrolledCredits() > 0) {
                totalSgpaSum += sheet.getSgpa();
                sgpaEligibleCount++;
            }

            for (CourseEvaluationResult cr : sheet.getCourseResults()) {
                String lg = cr.getLetterGrade();
                if (gradeDistribution.containsKey(lg)) {
                    gradeDistribution.put(lg, gradeDistribution.get(lg) + 1);
                }
            }
        }

        double classAvgSgpa = sgpaEligibleCount > 0 ? (totalSgpaSum / sgpaEligibleCount) : 0.0;
        double passPercentage = totalStudents > 0 ? ((passCount * 100.0) / totalStudents) : 0.0;

        // Sort students by SGPA descending
        sheetList.sort(Comparator.comparingDouble(StudentMarkSheet::getSgpa).reversed());

        summary.put("department", department.toUpperCase());
        summary.put("semester", semester);
        summary.put("totalStudents", totalStudents);
        summary.put("passCount", passCount);
        summary.put("arrearCount", arrearCount);
        summary.put("saCount", saCount);
        summary.put("incompleteCount", incompleteCount);
        summary.put("passPercentage", passPercentage);
        summary.put("classAvgSgpa", classAvgSgpa);
        summary.put("gradeDistribution", gradeDistribution);
        summary.put("topPerformers", sheetList);

        return summary;
    }

    /**
     * Resets database by executing the SQL schema script.
     */
    public boolean resetDatabase(String scriptPath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(scriptPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore SQL comments
                if (line.trim().startsWith("--") || line.trim().startsWith("/*")) {
                    continue;
                }
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading schema script: " + e.getMessage());
            return false;
        }

        String[] statements = sb.toString().split(";");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error executing schema script: " + e.getMessage());
            return false;
        }
    }
}

