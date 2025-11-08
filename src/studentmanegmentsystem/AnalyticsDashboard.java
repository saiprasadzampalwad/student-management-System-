package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Display analytics and stats.
 */
public class AnalyticsDashboard extends Frame implements ActionListener {
    private Button refreshBtn, backBtn;
    private Label totalStudentsLabel, avgGPALabel, totalFeesLabel, avgFeesLabel;

    public AnalyticsDashboard() {
        setTitle("Analytics Dashboard");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2));
        setLocationRelativeTo(null);

        // Labels
        add(new Label("Total Students:"));
        totalStudentsLabel = new Label("0");
        add(totalStudentsLabel);

        add(new Label("Average GPA:"));
        avgGPALabel = new Label("0.00");
        add(avgGPALabel);

        add(new Label("Total Fees Collected:"));
        totalFeesLabel = new Label("0.00");
        add(totalFeesLabel);

        add(new Label("Average Fees:"));
        avgFeesLabel = new Label("0.00");
        add(avgFeesLabel);

        refreshBtn = new Button("Refresh");
        backBtn = new Button("Back");

        refreshBtn.addActionListener(this);
        backBtn.addActionListener(this);

        add(refreshBtn);
        add(backBtn);

        // Load data
        loadAnalytics();

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
            loadAnalytics();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void loadAnalytics() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();

            // Total students
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM students");
            if (rs.next()) totalStudentsLabel.setText(String.valueOf(rs.getInt("count")));
            rs.close();
            stmt.close();

            // Avg GPA (avg of avg marks per student)
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT AVG(avg_marks) AS avg_gpa FROM (SELECT AVG(marks) AS avg_marks FROM marks GROUP BY student_id)");
            if (rs.next()) avgGPALabel.setText(String.format("%.2f", rs.getDouble("avg_gpa")));
            rs.close();
            stmt.close();

            // Total fees
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT SUM(tuition_fee + hostel_fee - scholarship) AS total FROM fees");
            if (rs.next()) totalFeesLabel.setText(String.format("%.2f", rs.getDouble("total")));
            rs.close();
            stmt.close();

            // Avg fees
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT AVG(tuition_fee + hostel_fee - scholarship) AS avg FROM fees");
            if (rs.next()) avgFeesLabel.setText(String.format("%.2f", rs.getDouble("avg")));
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            // Handle error, perhaps set labels to error
            totalStudentsLabel.setText("Error");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
