public class AdmissionRequest {
    private String requestedId;
    private String name;
    private String department;
    private int year;

    public AdmissionRequest(String requestedId, String name, String department, int year) {
        this.requestedId = requestedId;
        this.name = name;
        this.department = department;
        this.year = year;
    }

    public String getRequestedId() { return requestedId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }

    @Override
    public String toString() {
        return requestedId + " - " + name + " (" + department + ", Year " + year + ")";
    }
}