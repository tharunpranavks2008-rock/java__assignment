import java.sql.Timestamp;

/**
 * Course - Model representing a course offering under KEC R2024 regulations.
 */
public class Course {
    private String courseCode;
    private String title;
    private double credits;
    private CourseType courseType;
    private int semester;
    private Timestamp createdAt;

    public Course() {
    }

    public Course(String courseCode, String title, double credits, CourseType courseType, int semester) {
        this.courseCode = courseCode != null ? courseCode.trim().toUpperCase() : null;
        this.title = title != null ? title.trim() : null;
        this.credits = credits;
        this.courseType = courseType;
        this.semester = semester;
    }

    public Course(String courseCode, String title, double credits, CourseType courseType, int semester, Timestamp createdAt) {
        this(courseCode, title, credits, courseType, semester);
        this.createdAt = createdAt;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode != null ? courseCode.trim().toUpperCase() : null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-35s | Credits: %.1f | Type: %-9s | Sem: %d",
                courseCode, title, credits, courseType, semester);
    }
}

