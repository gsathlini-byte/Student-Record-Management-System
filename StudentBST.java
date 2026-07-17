import java.util.ArrayList;
import java.util.List;

public class StudentBST {

    private class Node {
        Student student;
        Node left, right;
        Node(Student student) { this.student = student; }
    }

    private Node root;
    private int count;

    public void insert(Student student) {
        root = insertRec(root, student);
    }

    private Node insertRec(Node node, Student student) {
        if (node == null) {
            count++;
            return new Node(student);
        }
        int cmp = student.getStudentId().compareTo(node.student.getStudentId());
        if (cmp < 0) {
            node.left = insertRec(node.left, student);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, student);
        } else {
            node.student = student; // update existing
        }
        return node;
    }

    public Student search(String studentId) {
        Node current = root;
        while (current != null) {
            int cmp = studentId.compareTo(current.student.getStudentId());
            if (cmp == 0) return current.student;
            current = (cmp < 0) ? current.left : current.right;
        }
        return null;
    }

    public boolean delete(String studentId) {
        if (search(studentId) == null) return false;
        root = deleteRec(root, studentId);
        count--;
        return true;
    }

    private Node deleteRec(Node node, String studentId) {
        if (node == null) return null;
        int cmp = studentId.compareTo(node.student.getStudentId());
        if (cmp < 0) {
            node.left = deleteRec(node.left, studentId);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, studentId);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            // දරුවන් දෙන්නෙක්ම ඉන්නවා නම්: දකුණු subtree එකේ ලොකුම කුඩාම value එක හොයනවා
            Node successor = node.right;
            while (successor.left != null) successor = successor.left;
            node.student = successor.student;
            node.right = deleteRec(node.right, successor.student.getStudentId());
        }
        return node;
    }

    /** in-order traversal එකෙන් students ID අනුව sorted විදිහට එනවා */
    public List<Student> inOrder() {
        List<Student> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node node, List<Student> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.student);
        inOrderRec(node.right, result);
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return root == null;
    }
}