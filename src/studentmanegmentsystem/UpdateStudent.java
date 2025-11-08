package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Update student details by roll_no.
 */
public class UpdateStudent extends Frame implements ActionListener {
    private TextField rollNoField, nameField, branchField, ageField;
    private Choice courseChoice;
    private Button searchBtn, updateBtn, backBtn;
    private Label messageLabel;
    private int studentId = -1;

    public UpdateStudent() {
        setTitle("Update Student");
        setSize(400, 400);
        setLayout(new GridLayout(9, 2));
        setLocationRelativeTo(null);

        // Components
        Label rollNoLabel = new Label("Roll No:");
        rollNoField = new TextField();

        searchBtn = new Button("Search");

        Label nameLabel = new Label("Name:");
        nameField = new TextField();

        Label branchLabel = new Label("Branch:");
        branchField = new TextField();

        Label ageLabel = new Label("Age:");
        ageField = new TextField();

        Label courseLabel = new Label("Course:");
        courseChoice = new Choice();
        loadCourses();

        updateBtn = new Button("Update");
        backBtn = new Button("Back");

        messageLabel = new Label("");
        messageLabel.setForeground(Color.BLUE);

        // Add listeners
        searchBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        backBtn.addActionListener(this);

        // Add components
        add(rollNoLabel);
        add(rollNoField);
        add(new Label(""));
        add(searchBtn);
        add(nameLabel);
        add(nameField);
        add(branchLabel);
        add(branchField);
        add(ageLabel);
        add(ageField);
        add(courseLabel);
        add(courseChoice);
        add(updateBtn);
        add(backBtn);
        add(new Label(""));
        add(messageLabel);

        // Initially disable update fields
        nameField.setEnabled(false);
        branchField.setEnabled(false);
        ageField.setEnabled(false);
        courseChoice.setEnabled(false);
        updateBtn.setEnabled(false);

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
        if (ae.getSource() == searchBtn) {
            String rollNo = rollNoField.getText().trim();
            if (rollNo.isEmpty()) {
                messageLabel.setText("Enter roll no to search.");
                return;
            }
            searchStudent(rollNo);
        } else if (ae.getSource() == updateBtn) {
            updateStudent();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void searchStudent(String rollNo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM students WHERE roll_no = ?");
            pstmt.setString(1, rollNo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                studentId = rs.getInt("id");
                nameField.setText(rs.getString("name"));
                branchField.setText(rs.getString("branch"));
                ageField.setText(String.valueOf(rs.getInt("age")));
                // Set course choice to current course
                int courseId = rs.getInt("course_id");
                for (int i = 0; i < courseChoice.getItemCount(); i++) {
                    String item = courseChoice.getItem(i);
                    int id = Integer.parseInt(item.split("ID: ")[1].replace(")", ""));
                    if (id == courseId) {
                        courseChoice.select(i);
                        break;
                    }
                }

                nameField.setEnabled(true);
                branchField.setEnabled(true);
                ageField.setEnabled(true);
                courseChoice.setEnabled(true);
                updateBtn.setEnabled(true);

                messageLabel.setText("Student found.");
            } else {
                messageLabel.setText("Student not found.");
                clearFields();
            }
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
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

    private void updateStudent() {
        String name = nameField.getText().trim();
        String branch = branchField.getText().trim();
        String ageStr = ageField.getText().trim();

        if (name.isEmpty() || branch.isEmpty() || ageStr.isEmpty() || courseChoice.getSelectedIndex() == -1) {
            messageLabel.setText("All fields are required.");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            String courseItem = courseChoice.getSelectedItem();
            int courseId = Integer.parseInt(courseItem.split("ID: ")[1].replace(")", ""));
            Connection conn = null;
            PreparedStatement pstmt = null;
            try {
                conn = DBConnection.getConnection();
                pstmt = conn.prepareStatement("UPDATE students SET name = ?, branch = ?, age = ?, course_id = ? WHERE id = ?");
                pstmt.setString(1, name);
                pstmt.setString(2, branch);
                pstmt.setInt(3, age);
                pstmt.setInt(4, courseId);
                pstmt.setInt(5, studentId);
                if (pstmt.executeUpdate() > 0) {
                    messageLabel.setText("Student updated successfully!");
                } else {
                    messageLabel.setText("Update failed.");
                }
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Age must be a number.");
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
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

    private void clearFields() {
        nameField.setText("");
        branchField.setText("");
        ageField.setText("");
        nameField.setEnabled(false);
        branchField.setEnabled(false);
        ageField.setEnabled(false);
        courseChoice.setEnabled(false);
        updateBtn.setEnabled(false);
        studentId = -1;
    }
}
