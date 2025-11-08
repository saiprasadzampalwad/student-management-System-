package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Search and filter students.
 */
public class SearchFilter extends Frame implements ActionListener {
    private Choice filterChoice;
    private TextField keywordField;
    private Button searchBtn, backBtn;
    private TextArea resultsArea;

    public SearchFilter() {
        setTitle("Search & Filter Students");
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // North panel
        Panel northPanel = new Panel(new GridLayout(2, 2));
        northPanel.add(new Label("Filter by:"));
        filterChoice = new Choice();
        filterChoice.add("Branch");
        filterChoice.add("Semester"); // But semester not in students table, perhaps branch for now
        northPanel.add(filterChoice);
        northPanel.add(new Label("Keyword:"));
        keywordField = new TextField();
        northPanel.add(keywordField);

        // Center
        resultsArea = new TextArea();
        resultsArea.setEditable(false);

        // South
        Panel southPanel = new Panel();
        searchBtn = new Button("Search");
        backBtn = new Button("Back");
        southPanel.add(searchBtn);
        southPanel.add(backBtn);

        // Listeners
        searchBtn.addActionListener(this);
        backBtn.addActionListener(this);

        // Add
        add(northPanel, BorderLayout.NORTH);
        add(resultsArea, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

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
            search();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void search() {
        String filter = filterChoice.getSelectedItem();
        String keyword = keywordField.getText().trim();
        resultsArea.setText("");
        String query = "SELECT * FROM students WHERE " + (filter.equals("Branch") ? "branch" : "branch") + " LIKE ?";
        // Since semester not in students, use branch for both
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "%" + keyword + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                resultsArea.append("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") +
                        ", Roll No: " + rs.getString("roll_no") + ", Branch: " + rs.getString("branch") +
                        ", Age: " + rs.getInt("age") + "\n");
            }
            if (resultsArea.getText().isEmpty()) {
                resultsArea.setText("No results found.");
            }
        } catch (SQLException e) {
            resultsArea.setText("Error: " + e.getMessage());
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
}
