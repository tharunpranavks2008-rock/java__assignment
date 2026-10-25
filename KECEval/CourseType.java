/**
 * CourseType - Course categories defined in Kongu Engineering College (R2024).
 * Each category has specific CIA & ESE weightages and passing thresholds:
 * - THEORY: CIA 40%, ESE 60%, ESE Threshold 45% (27/60), Pass 50
 * - PRACTICAL: CIA 60%, ESE 40%, ESE Threshold 50% (20/40), Pass 50
 * - EMBEDDED: CIA 50%, ESE 50%, ESE Threshold 45% (22.5/50), Pass 50
 */
public enum CourseType {
    THEORY(0.40, 0.60, 45.0, 50.0),
    PRACTICAL(0.60, 0.40, 50.0, 50.0),
    EMBEDDED(0.50, 0.50, 45.0, 50.0);

    private final double ciaWeightage;       // Fraction e.g. 0.40
    private final double eseWeightage;       // Fraction e.g. 0.60
    private final double eseMinThresholdPct; // Percentage out of 100 for ESE (e.g. 45% or 50%)
    private final double overallPassMin;     // Minimum total marks needed to pass (50.0)

    CourseType(double ciaWeightage, double eseWeightage, double eseMinThresholdPct, double overallPassMin) {
        this.ciaWeightage = ciaWeightage;
        this.eseWeightage = eseWeightage;
        this.eseMinThresholdPct = eseMinThresholdPct;
        this.overallPassMin = overallPassMin;
    }

    public double getCiaWeightage() {
        return ciaWeightage;
    }

    public double getEseWeightage() {
        return eseWeightage;
    }

    public double getEseMinThresholdPct() {
        return eseMinThresholdPct;
    }

    public double getOverallPassMin() {
        return overallPassMin;
    }

    /**
     * Minimum scaled marks required in ESE to satisfy autonomous threshold.
     */
    public double getEseMinScaledThreshold() {
        // e.g. For THEORY: 60 * 0.45 = 27.0
        // For PRACTICAL: 40 * 0.50 = 20.0
        // For EMBEDDED: 50 * 0.45 = 22.5
        return (eseWeightage * 100.0) * (eseMinThresholdPct / 100.0);
    }

    public static CourseType fromString(String text) {
        if (text == null) return THEORY;
        String upper = text.trim().toUpperCase();
        for (CourseType ct : values()) {
            if (ct.name().equals(upper)) {
                return ct;
            }
        }
        return THEORY;
    }
}

