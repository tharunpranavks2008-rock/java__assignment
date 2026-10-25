import java.sql.Timestamp;

/**
 * AssessmentMarks - Model representing phased incremental marks for an enrollment.
 * - Phase 1: CIA 1 (0-100), CIA 2 (0-100)
 * - Phase 2: Assignment / Quiz / Lab Evaluation (0-100)
 * - Phase 3: End Semester Exam (ESE) Marks (0-100) or Absence (AB)
 */
public class AssessmentMarks {
    private int markId;
    private int enrollmentId;
    private Double cia1Marks;
    private Double cia2Marks;
    private Double assignmentMarks;
    private Double eseMarks;
    private boolean isAbsentEse;
    private Timestamp updatedAt;

    public AssessmentMarks() {
    }

    public AssessmentMarks(int enrollmentId, Double cia1Marks, Double cia2Marks, Double assignmentMarks, Double eseMarks, boolean isAbsentEse) {
        this.enrollmentId = enrollmentId;
        this.cia1Marks = cia1Marks;
        this.cia2Marks = cia2Marks;
        this.assignmentMarks = assignmentMarks;
        this.eseMarks = eseMarks;
        this.isAbsentEse = isAbsentEse;
    }

    public AssessmentMarks(int markId, int enrollmentId, Double cia1Marks, Double cia2Marks, Double assignmentMarks, Double eseMarks, boolean isAbsentEse, Timestamp updatedAt) {
        this(enrollmentId, cia1Marks, cia2Marks, assignmentMarks, eseMarks, isAbsentEse);
        this.markId = markId;
        this.updatedAt = updatedAt;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Double getCia1Marks() {
        return cia1Marks;
    }

    public void setCia1Marks(Double cia1Marks) {
        this.cia1Marks = cia1Marks;
    }

    public Double getCia2Marks() {
        return cia2Marks;
    }

    public void setCia2Marks(Double cia2Marks) {
        this.cia2Marks = cia2Marks;
    }

    public Double getAssignmentMarks() {
        return assignmentMarks;
    }

    public void setAssignmentMarks(Double assignmentMarks) {
        this.assignmentMarks = assignmentMarks;
    }

    public Double getEseMarks() {
        return eseMarks;
    }

    public void setEseMarks(Double eseMarks) {
        this.eseMarks = eseMarks;
    }

    public boolean isAbsentEse() {
        return isAbsentEse;
    }

    public void setAbsentEse(boolean absentEse) {
        isAbsentEse = absentEse;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Checks if Phase 1 (CIA 1 & CIA 2) has been entered.
     */
    public boolean hasPhase1() {
        return cia1Marks != null && cia2Marks != null;
    }

    /**
     * Checks if Phase 2 (Assignments/Quizzes/Lab) has been entered.
     */
    public boolean hasPhase2() {
        return assignmentMarks != null;
    }

    /**
     * Checks if Phase 3 (ESE or Absent) has been recorded.
     */
    public boolean hasPhase3() {
        return isAbsentEse || eseMarks != null;
    }

    /**
     * Computes the Total CIA Marks on a 100-point scale:
     * CIA Tests account for 60% (CIA1 30% + CIA2 30%)
     * Assignment/Quiz/Lab accounts for 40%
     * Total = (CIA1 * 0.30) + (CIA2 * 0.30) + (Assignment * 0.40)
     */
    public Double calculateCiaTotal100() {
        if (!hasPhase1() || !hasPhase2()) {
            return null;
        }
        return (cia1Marks * 0.30) + (cia2Marks * 0.30) + (assignmentMarks * 0.40);
    }
}

