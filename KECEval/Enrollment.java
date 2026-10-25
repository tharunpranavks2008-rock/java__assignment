import java.sql.Timestamp;

/**
 * Enrollment - Model representing a student's enrollment in a course for a semester,
 * including continuous attendance tracking and medical condonation status.
 */
public class Enrollment {
    private int enrollmentId;
    private String rollNumber;
    private String courseCode;
    private int semester;
    private int classesHeld;
    private int classesAttended;
    private boolean medicalCondonation;
    private Timestamp createdAt;

    // Optional joined fields for display convenience
    private String studentName;
    private String courseTitle;
    private CourseType courseType;
    private double credits;

    public Enrollment() {
    }

    public Enrollment(String rollNumber, String courseCode, int semester, int classesHeld, int classesAttended, boolean medicalCondonation) {
        this.rollNumber = rollNumber != null ? rollNumber.trim().toUpperCase() : null;
        this.courseCode = courseCode != null ? courseCode.trim().toUpperCase() : null;
        this.semester = semester;
        this.classesHeld = classesHeld;
        this.classesAttended = classesAttended;
        this.medicalCondonation = medicalCondonation;
    }

    public Enrollment(int enrollmentId, String rollNumber, String courseCode, int semester, int classesHeld, int classesAttended, boolean medicalCondonation, Timestamp createdAt) {
        this(rollNumber, courseCode, semester, classesHeld, classesAttended, medicalCondonation);
        this.enrollmentId = enrollmentId;
        this.createdAt = createdAt;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber != null ? rollNumber.trim().toUpperCase() : null;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode != null ? courseCode.trim().toUpperCase() : null;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getClassesHeld() {
        return classesHeld;
    }

    public void setClassesHeld(int classesHeld) {
        this.classesHeld = classesHeld;
    }

    public int getClassesAttended() {
        return classesAttended;
    }

    public void setClassesAttended(int classesAttended) {
        this.classesAttended = classesAttended;
    }

    public boolean isMedicalCondonation() {
        return medicalCondonation;
    }

    public void setMedicalCondonation(boolean medicalCondonation) {
        this.medicalCondonation = medicalCondonation;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    /**
     * Calculates Attendance Percentage.
     * If classes held is 0, default to 100.0%.
     */
    public double getAttendancePercentage() {
        if (classesHeld <= 0) return 100.0;
        return (classesAttended * 100.0) / classesHeld;
    }

    /**
     * Enforces Kongu Engineering College (R2024) Attendance Filter:
     * - Attendance >= 75%: Eligible for ESE.
     * - Attendance between 65% and 74.9%: Conditionally eligible with approved Medical Condonation.
     * - Attendance < 65%: Barred from ESE (Award SA grade).
     * 
     * @return true if student is eligible to take ESE, false if barred (SA).
     */
    public boolean isEligibleForESE() {
        double pct = getAttendancePercentage();
        if (pct >= 75.0) {
            return true;
        } else if (pct >= 65.0 && medicalCondonation) {
            return true;
        }
        return false;
    }

    /**
     * Returns detailed textual attendance eligibility status.
     */
    public String getAttendanceStatusText() {
        double pct = getAttendancePercentage();
        if (pct >= 75.0) {
            return String.format("ELIGIBLE (%.1f%%)", pct);
        } else if (pct >= 65.0) {
            if (medicalCondonation) {
                return String.format("CONDONED (%.1f%% - Med Apprv)", pct);
            } else {
                return String.format("BARRED_SA (%.1f%% - No Condonation)", pct);
            }
        } else {
            return String.format("BARRED_SA (%.1f%% - Shortage <65%%)", pct);
        }
    }

    @Override
    public String toString() {
        return String.format("Enrollment #%d | %s -> %s | Attended: %d/%d (%.1f%%) | Status: %s",
                enrollmentId, rollNumber, courseCode, classesAttended, classesHeld,
                getAttendancePercentage(), getAttendanceStatusText());
    }
}

