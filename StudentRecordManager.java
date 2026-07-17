import java.util.List;

public class StudentRecordManager {

    private StudentBST bst;
    private CustomHashTable hashTable;
    private CustomStack<Action> undoStack;
    private CustomQueue<AdmissionRequest> admissionQueue;

    public StudentRecordManager() {
        bst = new StudentBST();
        hashTable = new CustomHashTable();
        undoStack = new CustomStack<>();
        admissionQueue = new CustomQueue<>();
    }

    // ---------- CRUD operations ----------

    public boolean addStudent(Student student) {
        if (hashTable.get(student.getStudentId()) != null) {
            return false; // දැනටමත් තියෙනවා
        }
        bst.insert(student);
        hashTable.put(student.getStudentId(), student);
        undoStack.push(new Action(Action.Type.ADD, student.getStudentId(), null));
        return true;
    }

    public boolean editStudent(String studentId, String name, String department, int year, double gpa) {
        Student existing = hashTable.get(studentId);
        if (existing == null) return false;
        // edit කරන්න කලින් snapshot එකක් ගන්නවා (undo කරන්න)
        Student snapshot = new Student(existing.getStudentId(), existing.getName(),
                existing.getDepartment(), existing.getYear(), existing.getGpa());
        undoStack.push(new Action(Action.Type.EDIT, studentId, snapshot));

        existing.setName(name);
        existing.setDepartment(department);
        existing.setYear(year);
        existing.setGpa(gpa);
        bst.insert(existing);
        return true;
    }

    public boolean deleteStudent(String studentId) {
        Student existing = hashTable.get(studentId);
        if (existing == null) return false;
        undoStack.push(new Action(Action.Type.DELETE, studentId, existing));
        bst.delete(studentId);
        hashTable.remove(studentId);
        return true;
    }

    public Student searchById(String studentId) {
        return hashTable.get(studentId); // Hash Table එකෙන් O(1) fast search
    }

    public List<Student> getAllSortedById() {
        return bst.inOrder(); // BST එකෙන් ID අනුව sorted list එක
    }

    public List<Student> getRankingByGpa() {
        return SortAlgorithms.mergeSortByGpaDescending(bst.inOrder());
    }

    public int totalStudents() {
        return hashTable.size();
    }

    // ---------- Undo ----------

    public String undoLastAction() {
        if (undoStack.isEmpty()) return "No actions to undo.";
        Action action = undoStack.pop();
        switch (action.getType()) {
            case ADD:
                bst.delete(action.getStudentId());
                hashTable.remove(action.getStudentId());
                return "Undid ADD for student " + action.getStudentId();
            case DELETE:
                Student restored = action.getStudentSnapshot();
                bst.insert(restored);
                hashTable.put(restored.getStudentId(), restored);
                return "Undid DELETE for student " + action.getStudentId();
            case EDIT:
                Student prev = action.getStudentSnapshot();
                Student current = hashTable.get(action.getStudentId());
                if (current != null) {
                    current.setName(prev.getName());
                    current.setDepartment(prev.getDepartment());
                    current.setYear(prev.getYear());
                    current.setGpa(prev.getGpa());
                }
                return "Undid EDIT for student " + action.getStudentId();
            default:
                return "Unknown action.";
        }
    }

    // ---------- Admission Queue ----------

    public void requestAdmission(AdmissionRequest request) {
        admissionQueue.enqueue(request);
    }

    public AdmissionRequest processNextAdmission() {
        return admissionQueue.dequeue();
    }

    public List<AdmissionRequest> pendingAdmissions() {
        return admissionQueue.toList();
    }

    public int pendingAdmissionCount() {
        return admissionQueue.size();
    }

    // ---------- Grade / Attendance history ----------

    public boolean addHistoryEntry(String studentId, String entry) {
        Student student = hashTable.get(studentId);
        if (student == null) return false;
        student.addHistoryEntry(entry);
        return true;
    }
}
