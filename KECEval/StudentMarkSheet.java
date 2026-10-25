import java.util.ArrayList;
import java.util.List;

/**
 * StudentMarkSheet - Aggregate performance report for a student in a specific semester.
 * Calculates SGPA, Equivalent Percentage, Credits Earned, and Academic Standing.
 */
public class StudentMarkSheet {
    private final Student student;
    private final int semester;
    private final List<CourseEvaluationResult> courseResults;

    private double totalEnrolledCredits;
    private double totalEarnedCredits;
    private double totalQualityPoints;
    private double sgpa;
    private double equivalentPercentage;
    private String standing; // "PASS", "RE-APPEARANCE (ARREAR)", "SHORTAGE OF ATTENDANCE (SA)", "PROVISIONAL"
    private int passCount;
    private int failCount;
    private int pendingCount;

    public StudentMarkSheet(Student student, int semester) {
        this.student = student;
        this.semester = semester;
        this.courseResults = new ArrayList<>();
    }

    public void addCourseResult(CourseEvaluationResult result) {
        this.courseResults.add(result);
        recalculate();
    }

    public void recalculate() {
        this.totalEnrolledCredits = 0.0;
        this.totalEarnedCredits = 0.0;
        this.totalQualityPoints = 0.0;
        this.passCount = 0;
        this.failCount = 0;
        this.pendingCount = 0;

        boolean hasSA = false;
        boolean hasAB = false;
        boolean hasRA = false;

        for (CourseEvaluationResult cr : courseResults) {
            double courseCredits = cr.getCourse().getCredits();
            this.totalEnrolledCredits += courseCredits;
            this.totalEarnedCredits += cr.getCreditsEarned();
            this.totalQualityPoints += cr.getQualityPoints();

            String grade = cr.getLetterGrade();
            if (cr.isPass()) {
                passCount++;
            } else if ("--".equals(grade)) {
                pendingCount++;
            } else {
                failCount++;
                if ("SA".equals(grade)) hasSA = true;
                else if ("AB".equals(grade)) hasAB = true;
                else if ("RA".equals(grade)) hasRA = true;
            }
        }

        if (totalEnrolledCredits > 0) {
            this.sgpa = totalQualityPoints / totalEnrolledCredits;
        } else {
            this.sgpa = 0.0;
        }

        this.equivalentPercentage = this.sgpa * 10.0;

        if (pendingCount > 0) {
            this.standing = "PROVISIONAL / INCOMPLETE";
        } else if (failCount == 0 && passCount > 0) {
            this.standing = "PASS";
        } else if (hasSA) {
            this.standing = "SHORTAGE OF ATTENDANCE (SA)";
        } else if (hasRA || hasAB) {
            this.standing = "RE-APPEARANCE (ARREAR)";
        } else {
            this.standing = "NO COURSES";
        }
    }

    public Student getStudent() {
        return student;
    }

    public int getSemester() {
        return semester;
    }

    public List<CourseEvaluationResult> getCourseResults() {
        return courseResults;
    }

    public double getTotalEnrolledCredits() {
        return totalEnrolledCredits;
    }

    public double getTotalEarnedCredits() {
        return totalEarnedCredits;
    }

    public double getTotalQualityPoints() {
        return totalQualityPoints;
    }

    public double getSgpa() {
        return sgpa;
    }

    public double getEquivalentPercentage() {
        return equivalentPercentage;
    }

    public String getStanding() {
        return standing;
    }

    public int getPassCount() {
        return passCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }
}

