package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Delete student by roll_no.
 */
public class DeleteStudent extends Frame implements ActionListener {
    private TextField rollNoField;
    private Button deleteBtn, backBtn;
    private Label messageLabel;

    public DeleteStudent() {
        setTitle("Delete Student");
        setSize(300, 200);
        setLayout(new GridLayout(4, 2));
        setLocationRelativeTo(null);

        // Components
        Label rollNoLabel = new Label("Roll No:");
        rollNoField = new TextField();

        deleteBtn = new Button("Delete");
        backBtn = new Button("Back");

        messageLabel = new Label("");
        messageLabel.setForeground(Color.RED);

        // Add listeners
        deleteBtn.addActionListener(this);
        backBtn.addActionListener(this);

        // Add components
        add(rollNoLabel);
        add(rollNoField);
        add(deleteBtn);
        add(backBtn);
        add(new Label(""));
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
        if (ae.getSource() == deleteBtn) {
            String rollNo = rollNoField.getText().trim();
            if (rollNo.isEmpty()) {
                messageLabel.setText("Enter roll no.");
                return;
            }
            if (deleteStudent(rollNo)) {
                messageLabel.setText("Student deleted successfully!");
                rollNoField.setText("");
            } else {
                messageLabel.setText("Student not found or delete failed.");
            }
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private boolean deleteStudent(String rollNo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("DELETE FROM students WHERE roll_no = ?");
            pstmt.setString(1, rollNo);
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
