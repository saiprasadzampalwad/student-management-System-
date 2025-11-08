package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Form to add a new student.
 */
public class AddStudent extends Frame implements ActionListener {
    private TextField nameField, rollNoField, branchField, ageField;
    private Choice courseChoice;
    private Button saveBtn, clearBtn, backBtn;
    private Label messageLabel;

    public AddStudent() {
        setTitle("Add Student");
        setSize(400, 350);
        setLayout(new GridLayout(7, 2));
        setLocationRelativeTo(null);

        // Components
        Label nameLabel = new Label("Name:");
        nameField = new TextField();

        Label rollNoLabel = new Label("Roll No:");
        rollNoField = new TextField();

        Label branchLabel = new Label("Branch:");
        branchField = new TextField();

        Label ageLabel = new Label("Age:");
        ageField = new TextField();

        Label courseLabel = new Label("Course:");
        courseChoice = new Choice();
        loadCourses();

        saveBtn = new Button("Save");
        clearBtn = new Button("Clear");
        backBtn = new Button("Back");

        messageLabel = new Label("");
        messageLabel.setForeground(Color.BLUE);

        // Add listeners
        saveBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        backBtn.addActionListener(this);

        // Add components
        add(nameLabel);
        add(nameField);
        add(rollNoLabel);
        add(rollNoField);
        add(branchLabel);
        add(branchField);
        add(ageLabel);
        add(ageField);
        add(courseLabel);
        add(courseChoice);
        add(saveBtn);
        add(clearBtn);
        add(backBtn);
        add(messageLabel);

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
        if (ae.getSource() == saveBtn) {
            String name = nameField.getText().trim();
            String rollNo = rollNoField.getText().trim();
            String branch = branchField.getText().trim();
            String ageStr = ageField.getText().trim();

            if (name.isEmpty() || rollNo.isEmpty() || branch.isEmpty() || ageStr.isEmpty() || courseChoice.getSelectedIndex() == -1) {
                messageLabel.setText("All fields are required.");
                return;
            }

            try {
                int age = Integer.parseInt(ageStr);
                String courseItem = courseChoice.getSelectedItem();
                int courseId = Integer.parseInt(courseItem.split("ID: ")[1].replace(")", ""));
                if (addStudent(name, rollNo, branch, age, courseId)) {
                    messageLabel.setText("Student added successfully!");
                    clearFields();
                } else {
                    messageLabel.setText("Failed to add student.");
                }
            } catch (NumberFormatException e) {
                messageLabel.setText("Age must be a number.");
            }
        } else if (ae.getSource() == clearBtn) {
            clearFields();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void clearFields() {
        nameField.setText("");
        rollNoField.setText("");
        branchField.setText("");
        ageField.setText("");
    }

    private void loadCourses() {
        courseChoice.removeAll();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT course_id, course_name FROM courses");
            while (rs.next()) {
                courseChoice.add(rs.getString("course_name") + " (ID: " + rs.getInt("course_id") + ")");
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

    private boolean addStudent(String name, String rollNo, String branch, int age, int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO students (name, roll_no, branch, age, course_id) VALUES (?, ?, ?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, rollNo);
            pstmt.setString(3, branch);
            pstmt.setInt(4, age);
            pstmt.setInt(5, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
