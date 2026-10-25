import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO - Data Access Object for Student, Course, and Enrollment management.
 */
public class StudentDAO {

    // =========================================================================
    // STUDENT CRUD
    // =========================================================================

    /**
     * Registers a new student into the system.
     */
    public boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (roll_number, name, department, current_semester) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getRollNumber());
            ps.setString(2, student.getName());
            ps.setString(3, student.getDepartment());
            ps.setInt(4, student.getCurrentSemester());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * Retrieves student by roll number.
     */
    public Student getStudent(String rollNumber) throws SQLException {
        String sql = "SELECT roll_number, name, department, current_semester, created_at FROM students WHERE roll_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("roll_number"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getInt("current_semester"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all registered students.
     */
    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT roll_number, name, department, current_semester, created_at FROM students ORDER BY roll_number ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("roll_number"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getInt("current_semester"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
        return list;
    }

    /**
     * Retrieves students filtered by department and optional semester.
     */
    public List<Student> getStudentsByDepartment(String dept, int semester) throws SQLException {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT roll_number, name, department, current_semester, created_at FROM students WHERE department = ?");
        if (semester > 0) {
            sql.append(" AND current_semester = ?");
        }
        sql.append(" ORDER BY roll_number ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, dept.trim().toUpperCase());
            if (semester > 0) {
                ps.setInt(2, semester);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                            rs.getString("roll_number"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getInt("current_semester"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        }
        return list;
    }

    // =========================================================================
    // COURSE CRUD
    // =========================================================================

    /**
     * Adds a new course to the course catalog.
     */
    public boolean addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, title, credits, course_type, semester) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getTitle());
            ps.setDouble(3, course.getCredits());
            ps.setString(4, course.getCourseType().name());
            ps.setInt(5, course.getSemester());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * Retrieves course by code.
     */
    public Course getCourse(String courseCode) throws SQLException {
        String sql = "SELECT course_code, title, credits, course_type, semester, created_at FROM courses WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getString("course_code"),
                            rs.getString("title"),
                            rs.getDouble("credits"),
                            CourseType.fromString(rs.getString("course_type")),
                            rs.getInt("semester"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all courses in the catalog.
     */
    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_code, title, credits, course_type, semester, created_at FROM courses ORDER BY course_code ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Course(
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getDouble("credits"),
                        CourseType.fromString(rs.getString("course_type")),
                        rs.getInt("semester"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
        return list;
    }

    /**
     * Retrieves courses for a specific semester.
     */
    public List<Course> getCoursesBySemester(int semester) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_code, title, credits, course_type, semester, created_at FROM courses WHERE semester = ? ORDER BY course_code ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Course(
                            rs.getString("course_code"),
                            rs.getString("title"),
                            rs.getDouble("credits"),
                            CourseType.fromString(rs.getString("course_type")),
                            rs.getInt("semester"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        }
        return list;
    }

    // =========================================================================
    // ENROLLMENT & ATTENDANCE TRACKING
    // =========================================================================

    /**
     * Enrolls a student in a course, initializes attendance tracking,
     * and sets up the assessment marks placeholder record.
     */
    public int enrollStudent(String rollNumber, String courseCode, int semester,
                             int classesHeld, int classesAttended, boolean condoned) throws SQLException {
        String insertEnrollSql = "INSERT INTO enrollments (roll_number, course_code, semester, classes_held, classes_attended, medical_condonation) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";
        String insertMarksSql = "INSERT INTO assessment_marks (enrollment_id) VALUES (?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int enrollmentId = -1;
                try (PreparedStatement ps = conn.prepareStatement(insertEnrollSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, rollNumber.trim().toUpperCase());
                    ps.setString(2, courseCode.trim().toUpperCase());
                    ps.setInt(3, semester);
                    ps.setInt(4, classesHeld);
                    ps.setInt(5, classesAttended);
                    ps.setBoolean(6, condoned);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            enrollmentId = rs.getInt(1);
                        }
                    }
                }

                if (enrollmentId > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(insertMarksSql)) {
                        ps.setInt(1, enrollmentId);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
                return enrollmentId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Updates attendance records and medical condonation for an enrollment.
     */
    public boolean updateAttendance(int enrollmentId, int classesHeld, int classesAttended, boolean condoned) throws SQLException {
        String sql = "UPDATE enrollments SET classes_held = ?, classes_attended = ?, medical_condonation = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classesHeld);
            ps.setInt(2, classesAttended);
            ps.setBoolean(3, condoned);
            ps.setInt(4, enrollmentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves enrollment by ID with joined student & course info.
     */
    public Enrollment getEnrollmentById(int enrollmentId) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.roll_number, e.course_code, e.semester, e.classes_held, " +
                     "e.classes_attended, e.medical_condonation, e.created_at, " +
                     "s.name AS student_name, c.title AS course_title, c.course_type, c.credits " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE e.enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Enrollment en = new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getString("roll_number"),
                            rs.getString("course_code"),
                            rs.getInt("semester"),
                            rs.getInt("classes_held"),
                            rs.getInt("classes_attended"),
                            rs.getBoolean("medical_condonation"),
                            rs.getTimestamp("created_at")
                    );
                    en.setStudentName(rs.getString("student_name"));
                    en.setCourseTitle(rs.getString("course_title"));
                    en.setCourseType(CourseType.fromString(rs.getString("course_type")));
                    en.setCredits(rs.getDouble("credits"));
                    return en;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves enrollment by student roll number, course code, and semester.
     */
    public Enrollment getEnrollment(String rollNumber, String courseCode, int semester) throws SQLException {
        String sql = "SELECT e.enrollment_id, e.roll_number, e.course_code, e.semester, e.classes_held, " +
                     "e.classes_attended, e.medical_condonation, e.created_at, " +
                     "s.name AS student_name, c.title AS course_title, c.course_type, c.credits " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE e.roll_number = ? AND e.course_code = ? AND e.semester = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber.trim().toUpperCase());
            ps.setString(2, courseCode.trim().toUpperCase());
            ps.setInt(3, semester);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Enrollment en = new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getString("roll_number"),
                            rs.getString("course_code"),
                            rs.getInt("semester"),
                            rs.getInt("classes_held"),
                            rs.getInt("classes_attended"),
                            rs.getBoolean("medical_condonation"),
                            rs.getTimestamp("created_at")
                    );
                    en.setStudentName(rs.getString("student_name"));
                    en.setCourseTitle(rs.getString("course_title"));
                    en.setCourseType(CourseType.fromString(rs.getString("course_type")));
                    en.setCredits(rs.getDouble("credits"));
                    return en;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all course enrollments for a student in a semester.
     */
    public List<Enrollment> getEnrollmentsByStudent(String rollNumber, int semester) throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.roll_number, e.course_code, e.semester, e.classes_held, " +
                     "e.classes_attended, e.medical_condonation, e.created_at, " +
                     "s.name AS student_name, c.title AS course_title, c.course_type, c.credits " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE e.roll_number = ? AND e.semester = ? " +
                     "ORDER BY e.course_code ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber.trim().toUpperCase());
            ps.setInt(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment en = new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getString("roll_number"),
                            rs.getString("course_code"),
                            rs.getInt("semester"),
                            rs.getInt("classes_held"),
                            rs.getInt("classes_attended"),
                            rs.getBoolean("medical_condonation"),
                            rs.getTimestamp("created_at")
                    );
                    en.setStudentName(rs.getString("student_name"));
                    en.setCourseTitle(rs.getString("course_title"));
                    en.setCourseType(CourseType.fromString(rs.getString("course_type")));
                    en.setCredits(rs.getDouble("credits"));
                    list.add(en);
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all enrollments for a given semester across all students.
     */
    public List<Enrollment> getAllEnrollments(int semester) throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.roll_number, e.course_code, e.semester, e.classes_held, " +
                     "e.classes_attended, e.medical_condonation, e.created_at, " +
                     "s.name AS student_name, c.title AS course_title, c.course_type, c.credits " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_number = s.roll_number " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE e.semester = ? " +
                     "ORDER BY e.roll_number ASC, e.course_code ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment en = new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getString("roll_number"),
                            rs.getString("course_code"),
                            rs.getInt("semester"),
                            rs.getInt("classes_held"),
                            rs.getInt("classes_attended"),
                            rs.getBoolean("medical_condonation"),
                            rs.getTimestamp("created_at")
                    );
                    en.setStudentName(rs.getString("student_name"));
                    en.setCourseTitle(rs.getString("course_title"));
                    en.setCourseType(CourseType.fromString(rs.getString("course_type")));
                    en.setCredits(rs.getDouble("credits"));
                    list.add(en);
                }
            }
        }
        return list;
    }
}

