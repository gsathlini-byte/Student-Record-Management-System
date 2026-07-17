import java.io.Serializable;

public class Student implements Serializable {
    private String studentId;
    private String name;
    private String department;
    private int year;
    private double gpa;
    private CustomLinkedList history; // student ගේ grade/attendance history

    public Student(String studentId, String name, String department, int year, double gpa) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.gpa = gpa;
        this.history = new CustomLinkedList();
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }
    public double getGpa() { return gpa; }
    public CustomLinkedList getHistory() { return history; }

    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setYear(int year) { this.year = year; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public void addHistoryEntry(String entry) {
        history.addLast(entry);
    }

    @Override
    public String toString() {
        return String.format("%-10s %-20s %-15s %-6d %-5.2f", studentId, name, department, year, gpa);
    }
}