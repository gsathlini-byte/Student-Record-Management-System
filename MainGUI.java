import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainGUI extends JFrame {

    private StudentRecordManager manager;

    private DefaultTableModel tableModel;
    private JTable studentTable;

    private JTextField idField, nameField, deptField, yearField, gpaField, searchField;
    private JLabel statusLabel;

    private DefaultTableModel queueModel;
    private JTable queueTable;
    private JTextField reqIdField, reqNameField, reqDeptField, reqYearField;

    private DefaultTableModel rankingModel;
    private JTable rankingTable;

    public MainGUI() {
        manager = new StudentRecordManager();
        seedSampleData();

        setTitle("Student Record Management System - ICT 143-2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Student Records", buildStudentPanel());
        tabs.addTab("Admission Queue", buildQueuePanel());
        tabs.addTab("GPA Ranking", buildRankingPanel());

        add(tabs);
        refreshStudentTable();
    }

    private void seedSampleData() {
        manager.addStudent(new Student("UWU001", "Nimal Perera", "ICT", 2, 3.65));
        manager.addStudent(new Student("UWU002", "Kamala Silva", "ICT", 3, 3.90));
        manager.addStudent(new Student("UWU003", "Sunil Fernando", "Agri Tech", 1, 3.20));
        manager.addStudent(new Student("UWU004", "Anusha Jayasuriya", "ICT", 2, 3.45));
    }

    // ---------------- Student Records Tab ----------------

    private JPanel buildStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(2, 6, 5, 5));
        idField = new JTextField();
        nameField = new JTextField();
        deptField = new JTextField();
        yearField = new JTextField();
        gpaField = new JTextField();

        form.add(new JLabel("Student ID:"));
        form.add(idField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Department:"));
        form.add(deptField);
        form.add(new JLabel("Year:"));
        form.add(yearField);
        form.add(new JLabel("GPA:"));
        form.add(gpaField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton undoBtn = new JButton("Undo Last Action");
        JButton clearBtn = new JButton("Clear Fields");
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(undoBtn);
        buttons.add(clearBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(form, BorderLayout.CENTER);
        topPanel.add(buttons, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search by ID (Hash Table)");
        JButton showAllBtn = new JButton("Show All (Sorted by ID - BST)");
        searchPanel.add(new JLabel("Search ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Department", "Year", "GPA"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        studentTable = new JTable(tableModel);
        studentTable.getSelectionModel().addListSelectionListener(e -> loadSelectedRowIntoForm());
        JScrollPane scrollPane = new JScrollPane(studentTable);

        statusLabel = new JLabel("Ready.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        addBtn.addActionListener(this::handleAdd);
        editBtn.addActionListener(this::handleEdit);
        deleteBtn.addActionListener(this::handleDelete);
        undoBtn.addActionListener(this::handleUndo);
        clearBtn.addActionListener(e -> clearForm());
        searchBtn.addActionListener(this::handleSearch);
        showAllBtn.addActionListener(e -> refreshStudentTable());

        return panel;
    }

    private void handleAdd(ActionEvent e) {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            double gpa = Double.parseDouble(gpaField.getText().trim());
            if (id.isEmpty() || name.isEmpty()) {
                status("Student ID and Name are required.");
                return;
            }
            boolean added = manager.addStudent(new Student(id, name, dept, year, gpa));
            if (added) {
                status("Added student " + id + ".");
                refreshStudentTable();
                clearForm();
            } else {
                status("Student ID " + id + " already exists.");
            }
        } catch (NumberFormatException ex) {
            status("Year must be an integer and GPA must be a number.");
        }
    }

    private void handleEdit(ActionEvent e) {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            double gpa = Double.parseDouble(gpaField.getText().trim());
            boolean edited = manager.editStudent(id, name, dept, year, gpa);
            if (edited) {
                status("Updated student " + id + ".");
                refreshStudentTable();
                clearForm();
            } else {
                status("Student ID " + id + " not found.");
            }
        } catch (NumberFormatException ex) {
            status("Year must be an integer and GPA must be a number.");
        }
    }

    private void handleDelete(ActionEvent e) {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            status("Enter a Student ID to delete.");
            return;
        }
        boolean deleted = manager.deleteStudent(id);
        status(deleted ? "Deleted student " + id + "." : "Student ID " + id + " not found.");
        refreshStudentTable();
        clearForm();
    }

    private void handleUndo(ActionEvent e) {
        String result = manager.undoLastAction();
        status(result);
        refreshStudentTable();
    }

    private void handleSearch(ActionEvent e) {
        String id = searchField.getText().trim();
        Student s = manager.searchById(id);
        if (s == null) {
            status("No student found with ID " + id);
            tableModel.setRowCount(0);
            return;
        }
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{s.getStudentId(), s.getName(), s.getDepartment(), s.getYear(), s.getGpa()});
        status("Found student " + id + " (Hash Table lookup).");
    }

    private void loadSelectedRowIntoForm() {
        int row = studentTable.getSelectedRow();
        if (row < 0) return;
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        deptField.setText(tableModel.getValueAt(row, 2).toString());
        yearField.setText(tableModel.getValueAt(row, 3).toString());
        gpaField.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        deptField.setText("");
        yearField.setText("");
        gpaField.setText("");
        searchField.setText("");
    }

    private void refreshStudentTable() {
        tableModel.setRowCount(0);
        List<Student> students = manager.getAllSortedById();
        for (Student s : students) {
            tableModel.addRow(new Object[]{s.getStudentId(), s.getName(), s.getDepartment(), s.getYear(), s.getGpa()});
        }
        if (rankingModel != null) refreshRankingTable();
    }

    private void status(String msg) {
        statusLabel.setText(msg);
    }

    // ---------------- Admission Queue Tab ----------------

    private JPanel buildQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(1, 8, 5, 5));
        reqIdField = new JTextField();
        reqNameField = new JTextField();
        reqDeptField = new JTextField();
        reqYearField = new JTextField();
        form.add(new JLabel("New ID:"));
        form.add(reqIdField);
        form.add(new JLabel("Name:"));
        form.add(reqNameField);
        form.add(new JLabel("Department:"));
        form.add(reqDeptField);
        form.add(new JLabel("Year:"));
        form.add(reqYearField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton enqueueBtn = new JButton("Request Admission (Enqueue)");
        JButton processBtn = new JButton("Process Next Request (Dequeue)");
        buttons.add(enqueueBtn);
        buttons.add(processBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        queueModel = new DefaultTableModel(new Object[]{"Requested ID", "Name", "Department", "Year"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        queueTable = new JTable(queueModel);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

        enqueueBtn.addActionListener(e -> {
            try {
                String id = reqIdField.getText().trim();
                String name = reqNameField.getText().trim();
                String dept = reqDeptField.getText().trim();
                int year = Integer.parseInt(reqYearField.getText().trim());
                if (id.isEmpty() || name.isEmpty()) return;
                manager.requestAdmission(new AdmissionRequest(id, name, dept, year));
                refreshQueueTable();
                reqIdField.setText("");
                reqNameField.setText("");
                reqDeptField.setText("");
                reqYearField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Year must be an integer.");
            }
        });

        processBtn.addActionListener(e -> {
            AdmissionRequest req = manager.processNextAdmission();
            if (req == null) {
                JOptionPane.showMessageDialog(this, "No pending admission requests.");
                return;
            }
            manager.addStudent(new Student(req.getRequestedId(), req.getName(), req.getDepartment(), req.getYear(), 0.0));
            refreshQueueTable();
            refreshStudentTable();
            JOptionPane.showMessageDialog(this, "Admitted: " + req);
        });

        return panel;
    }

    private void refreshQueueTable() {
        queueModel.setRowCount(0);
        for (AdmissionRequest r : manager.pendingAdmissions()) {
            queueModel.addRow(new Object[]{r.getRequestedId(), r.getName(), r.getDepartment(), r.getYear()});
        }
    }

    // ---------------- GPA Ranking Tab ----------------

    private JPanel buildRankingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshBtn = new JButton("Refresh Ranking (Merge Sort by GPA)");
        rankingModel = new DefaultTableModel(new Object[]{"Rank", "ID", "Name", "GPA"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        rankingTable = new JTable(rankingModel);

        panel.add(refreshBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(rankingTable), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> refreshRankingTable());
        return panel;
    }

    private void refreshRankingTable() {
        rankingModel.setRowCount(0);
        List<Student> ranked = manager.getRankingByGpa();
        int rank = 1;
        for (Student s : ranked) {
            rankingModel.addRow(new Object[]{rank++, s.getStudentId(), s.getName(), s.getGpa()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}