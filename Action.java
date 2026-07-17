public class Action {
    public enum Type { ADD, DELETE, EDIT }

    private Type type;
    private Student studentSnapshot; // action එකට කලින් තිබ්බ state එක (EDIT/DELETE undo කරන්න)
    private String studentId;

    public Action(Type type, String studentId, Student studentSnapshot) {
        this.type = type;
        this.studentId = studentId;
        this.studentSnapshot = studentSnapshot;
    }

    public Type getType() { return type; }
    public Student getStudentSnapshot() { return studentSnapshot; }
    public String getStudentId() { return studentId; }
}