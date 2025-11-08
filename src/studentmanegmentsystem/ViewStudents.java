package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * View all students in a TextArea.
 */
public class ViewStudents extends Frame implements ActionListener {
    private TextArea studentsArea;
    private Button refreshBtn, backBtn;

    public ViewStudents() {
        setTitle("View Students");
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Components
        studentsArea = new TextArea();
        studentsArea.setEditable(false);

        Panel buttonPanel = new Panel();
        refreshBtn = new Button("Refresh");
        backBtn = new Button("Back");

        refreshBtn.addActionListener(this);
        backBtn.addActionListener(this);

        buttonPanel.add(refreshBtn);
        buttonPanel.add(backBtn);

        // Add components
        add(studentsArea, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load students
        loadStudents();

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
        if (ae.getSource() == refreshBtn) {
            loadStudents();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void loadStudents() {
        studentsArea.setText("");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM students");
            while (rs.next()) {
                studentsArea.append("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                        ", Roll No: " + rs.getString("roll_no") + ", Branch: " + rs.getString("branch") +
                        ", Age: " + rs.getInt("age") + "\n");
            }
        } catch (SQLException e) {
            studentsArea.setText("Error loading students: " + e.getMessage());
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
}
