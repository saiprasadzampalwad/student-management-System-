package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Enter marks for students and calculate grades.
 */
public class MarksEntry extends Frame implements ActionListener {
    private Choice studentChoice, subjectChoice;
    private TextField marksField;
    private Button saveMarksBtn, calculateGradeBtn, backBtn;
    private Label messageLabel, gradeLabel;

    public MarksEntry() {
        setTitle("Marks Entry & Grade Calculation");
        setSize(400, 300);
        setLayout(new GridLayout(7, 2));
        setLocationRelativeTo(null);

        // Components
        Label studentLabel = new Label("Select Student:");
        studentChoice = new Choice();
        loadStudents();

        Label subjectLabel = new Label("Select Subject:");
        subjectChoice = new Choice();

        Label marksLabel = new Label("Marks:");
        marksField = new TextField();

        saveMarksBtn = new Button("Save Marks");
        calculateGradeBtn = new Button("Calculate Grade");
        backBtn = new Button("Back");

        messageLabel = new Label("");
        messageLabel.setForeground(Color.BLUE);

        gradeLabel = new Label("");
        gradeLabel.setForeground(Color.GREEN);

        // Add listeners
        studentChoice.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                loadSubjectsForStudent();
            }
        });
        saveMarksBtn.addActionListener(this);
        calculateGradeBtn.addActionListener(this);
        backBtn.addActionListener(this);

        // Add components
        add(studentLabel);
        add(studentChoice);
        add(subjectLabel);
        add(subjectChoice);
        add(marksLabel);
        add(marksField);
        add(saveMarksBtn);
        add(calculateGradeBtn);
        add(backBtn);
        add(messageLabel);
        add(new Label("Grade:"));
        add(gradeLabel);

        // Window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == saveMarksBtn) {
            saveMarks();
        } else if (ae.getSource() == calculateGradeBtn) {
            calculateGrade();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void loadStudents() {
        studentChoice.removeAll();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id, name, roll_no FROM students");
            while (rs.next()) {
                studentChoice.add(rs.getString("name") + " (" + rs.getString("roll_no") + ") ID: " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSubjectsForStudent() {
        subjectChoice.removeAll();
        if (studentChoice.getSelectedIndex() == -1) return;
        String studentItem = studentChoice.getSelectedItem();
        int studentId = Integer.parseInt(studentItem.split("ID: ")[1]);
        // Load subjects based on student's course
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("SELECT s.subject_id, s.subject_name FROM subjects s JOIN students st ON s.course_id = st.course_id WHERE st.id = ?");
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                subjectChoice.add(rs.getString("subject_name") + " ID: " + rs.getInt("subject_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveMarks() {
        if (studentChoice.getSelectedIndex() == -1 || subjectChoice.getSelectedIndex() == -1) {
            messageLabel.setText("Select student and subject.");
            return;
        }
        String marksStr = marksField.getText().trim();
        if (marksStr.isEmpty()) {
            messageLabel.setText("Enter marks.");
            return;
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            int marks = Integer.parseInt(marksStr);
            String studentItem = studentChoice.getSelectedItem();
            int studentId = Integer.parseInt(studentItem.split("ID: ")[1]);
            String subjectItem = subjectChoice.getSelectedItem();
            int subjectId = Integer.parseInt(subjectItem.split("ID: ")[1]);

            conn = DBConnection.getConnection();
            if (marksExist(studentId, subjectId)) {
                pstmt = conn.prepareStatement("UPDATE marks SET marks = ? WHERE student_id = ? AND subject_id = ?");
                pstmt.setInt(1, marks);
                pstmt.setInt(2, studentId);
                pstmt.setInt(3, subjectId);
            } else {
                pstmt = conn.prepareStatement("INSERT INTO marks (student_id, subject_id, marks) VALUES (?, ?, ?)");
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, subjectId);
                pstmt.setInt(3, marks);
            }
            if (pstmt.executeUpdate() > 0) {
                messageLabel.setText("Marks saved.");
                marksField.setText("");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Marks must be a number.");
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean marksExist(int studentId, int subjectId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("SELECT 1 FROM marks WHERE student_id = ? AND subject_id = ?");
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            rs = pstmt.executeQuery();
            return rs.next();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void calculateGrade() {
        if (studentChoice.getSelectedIndex() == -1) {
            gradeLabel.setText("Select a student.");
            return;
        }
        String studentItem = studentChoice.getSelectedItem();
        int studentId = Integer.parseInt(studentItem.split("ID: ")[1]);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("SELECT AVG(marks) AS avg_marks FROM marks WHERE student_id = ?");
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                double avg = rs.getDouble("avg_marks");
                if (rs.wasNull()) {
                    gradeLabel.setText("No marks found.");
                } else {
                    String grade = getGrade(avg);
                    gradeLabel.setText(String.format("%.2f (%s)", avg, grade));
                }
            }
        } catch (SQLException e) {
            gradeLabel.setText("Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String getGrade(double avg) {
        if (avg >= 90) return "A";
        else if (avg >= 80) return "B";
        else if (avg >= 70) return "C";
        else if (avg >= 60) return "D";
        else return "F";
    }
}
