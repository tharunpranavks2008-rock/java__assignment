import java.sql.Timestamp;

/**
 * Student - Model representing a student enrolled at Kongu Engineering College.
 */
public class Student {
    private String rollNumber;
    private String name;
    private String department;
    private int currentSemester;
    private Timestamp createdAt;

    public Student() {
    }

    public Student(String rollNumber, String name, String department, int currentSemester) {
        this.rollNumber = rollNumber != null ? rollNumber.trim().toUpperCase() : null;
        this.name = name != null ? name.trim() : null;
        this.department = department != null ? department.trim().toUpperCase() : null;
        this.currentSemester = currentSemester;
    }

    public Student(String rollNumber, String name, String department, int currentSemester, Timestamp createdAt) {
        this(rollNumber, name, department, currentSemester);
        this.createdAt = createdAt;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber != null ? rollNumber.trim().toUpperCase() : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null ? department.trim().toUpperCase() : null;
    }

    public int getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(int currentSemester) {
        this.currentSemester = currentSemester;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Dept: %-5s | Sem: %d",
                rollNumber, name, department, currentSemester);
    }
}

