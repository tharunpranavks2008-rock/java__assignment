/**
 * CourseEvaluationResult - Holds the comprehensive evaluation breakdown for a single course enrollment
 * under Kongu Engineering College (Autonomous) - Regulation 2024.
 */
public class CourseEvaluationResult {
    private final Enrollment enrollment;
    private final Course course;
    private final AssessmentMarks marks;

    // Computed attributes
    private double attendancePct;
    private boolean eligibleForESE;
    private String attendanceStatus;

    private Double ciaTotal100;
    private Double scaledCia;
    private Double eseMarks;
    private Double scaledEse;
    private Double totalMarks;
    private int roundedTotalMarks;

    private boolean eseThresholdPassed;
    private boolean overallPassed;
    private String letterGrade;
    private int gradePoint;
    private double creditsEarned;
    private double qualityPoints;
    private String resultStatus;

    public CourseEvaluationResult(Enrollment enrollment, Course course, AssessmentMarks marks) {
        this.enrollment = enrollment;
        this.course = course;
        this.marks = marks != null ? marks : new AssessmentMarks();
        computeEvaluation();
    }

    private void computeEvaluation() {
        this.attendancePct = enrollment.getAttendancePercentage();
        this.eligibleForESE = enrollment.isEligibleForESE();
        this.attendanceStatus = enrollment.getAttendanceStatusText();

        // 1. Check Attendance Filter First
        if (!eligibleForESE) {
            this.letterGrade = "SA";
            this.gradePoint = 0;
            this.creditsEarned = 0.0;
            this.qualityPoints = 0.0;
            this.resultStatus = "SHORTAGE OF ATTENDANCE (SA)";
            this.totalMarks = 0.0;
            this.roundedTotalMarks = 0;
            return;
        }

        // 2. Check Absent in ESE
        if (marks.isAbsentEse()) {
            this.letterGrade = "AB";
            this.gradePoint = 0;
            this.creditsEarned = 0.0;
            this.qualityPoints = 0.0;
            this.resultStatus = "ABSENT IN ESE (AB)";
            this.totalMarks = 0.0;
            this.roundedTotalMarks = 0;
            return;
        }

        // 3. Check CIA calculation
        this.ciaTotal100 = marks.calculateCiaTotal100();
        if (ciaTotal100 != null) {
            this.scaledCia = ciaTotal100 * course.getCourseType().getCiaWeightage();
        }

        // 4. Check ESE marks
        this.eseMarks = marks.getEseMarks();
        if (eseMarks == null) {
            // ESE is still pending
            this.letterGrade = "--";
            this.gradePoint = 0;
            this.creditsEarned = 0.0;
            this.qualityPoints = 0.0;
            this.resultStatus = "PENDING ESE";
            return;
        }

        this.scaledEse = eseMarks * course.getCourseType().getEseWeightage();

        // If CIA marks were missing, default to 0 for scaled calculation
        double effectiveScaledCia = scaledCia != null ? scaledCia : 0.0;
        this.totalMarks = effectiveScaledCia + this.scaledEse;
        this.roundedTotalMarks = (int) Math.round(this.totalMarks);

        // 5. Enforce KEC R2024 Thresholds
        CourseType type = course.getCourseType();
        this.eseThresholdPassed = (eseMarks >= type.getEseMinThresholdPct());
        this.overallPassed = (this.totalMarks >= type.getOverallPassMin());

        if (!eseThresholdPassed) {
            this.letterGrade = "RA";
            this.gradePoint = 0;
            this.creditsEarned = 0.0;
            this.resultStatus = String.format("FAIL (ESE < %.0f%%: %.1f/%.0f)",
                    type.getEseMinThresholdPct(), scaledEse, type.getEseWeightage() * 100);
        } else if (!overallPassed) {
            this.letterGrade = "RA";
            this.gradePoint = 0;
            this.creditsEarned = 0.0;
            this.resultStatus = String.format("FAIL (Total < %.0f: %.1f)",
                    type.getOverallPassMin(), totalMarks);
        } else {
            // Passed! Determine 10-point Letter Grade
            this.creditsEarned = course.getCredits();
            this.resultStatus = "PASS";

            if (roundedTotalMarks >= 91) {
                this.letterGrade = "O";
                this.gradePoint = 10;
            } else if (roundedTotalMarks >= 81) {
                this.letterGrade = "A+";
                this.gradePoint = 9;
            } else if (roundedTotalMarks >= 71) {
                this.letterGrade = "A";
                this.gradePoint = 8;
            } else if (roundedTotalMarks >= 61) {
                this.letterGrade = "B+";
                this.gradePoint = 7;
            } else if (roundedTotalMarks >= 50) {
                this.letterGrade = "B";
                this.gradePoint = 6;
            } else {
                this.letterGrade = "RA";
                this.gradePoint = 0;
                this.creditsEarned = 0.0;
                this.resultStatus = "FAIL (Below 50)";
            }
        }

        this.qualityPoints = this.creditsEarned * this.gradePoint;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public Course getCourse() {
        return course;
    }

    public AssessmentMarks getMarks() {
        return marks;
    }

    public double getAttendancePct() {
        return attendancePct;
    }

    public boolean isEligibleForESE() {
        return eligibleForESE;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public Double getCiaTotal100() {
        return ciaTotal100;
    }

    public Double getScaledCia() {
        return scaledCia;
    }

    public Double getEseMarks() {
        return eseMarks;
    }

    public Double getScaledEse() {
        return scaledEse;
    }

    public Double getTotalMarks() {
        return totalMarks;
    }

    public int getRoundedTotalMarks() {
        return roundedTotalMarks;
    }

    public boolean isEseThresholdPassed() {
        return eseThresholdPassed;
    }

    public boolean isOverallPassed() {
        return overallPassed;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public int getGradePoint() {
        return gradePoint;
    }

    public double getCreditsEarned() {
        return creditsEarned;
    }

    public double getQualityPoints() {
        return qualityPoints;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public boolean isPass() {
        return "PASS".equals(resultStatus);
    }
}

