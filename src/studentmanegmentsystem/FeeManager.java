package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;

/**
 * Manage student fees.
 */
public class FeeManager extends Frame implements ActionListener {
    private Choice studentChoice;
    private TextField tuitionField, hostelField, scholarshipField, totalField;
    private Button calculateBtn, saveBtn, printBtn, backBtn;
    private Label messageLabel;

    public FeeManager() {
        setTitle("Fee Manager");
        setSize(400, 350);
        setLayout(new GridLayout(8, 2));
        setLocationRelativeTo(null);

        // Components
        Label studentLabel = new Label("Select Student:");
        studentChoice = new Choice();
        loadStudents();

        Label tuitionLabel = new Label("Tuition Fee:");
        tuitionField = new TextField();

        Label hostelLabel = new Label("Hostel Fee:");
        hostelField = new TextField();

        Label scholarshipLabel = new Label("Scholarship:");
        scholarshipField = new TextField();

        Label totalLabel = new Label("Total Fee:");
        totalField = new TextField();
        totalField.setEditable(false);

        calculateBtn = new Button("Calculate Total");
        saveBtn = new Button("Save Fees");
        printBtn = new Button("Print Receipt");
        backBtn = new Button("Back");

        messageLabel = new Label("");
        messageLabel.setForeground(Color.BLUE);

        // Add listeners
        studentChoice.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                loadFees();
            }
        });
        calculateBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        printBtn.addActionListener(this);
        backBtn.addActionListener(this);

        // Add components
        add(studentLabel);
        add(studentChoice);
        add(tuitionLabel);
        add(tuitionField);
        add(hostelLabel);
        add(hostelField);
        add(scholarshipLabel);
        add(scholarshipField);
        add(totalLabel);
        add(totalField);
        add(calculateBtn);
        add(saveBtn);
        add(printBtn);
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
        if (ae.getSource() == calculateBtn) {
            calculateTotal();
        } else if (ae.getSource() == saveBtn) {
            saveFees();
        } else if (ae.getSource() == printBtn) {
            printReceipt();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void loadStudents() {
        studentChoice.removeAll();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, roll_no FROM students")) {
            while (rs.next()) {
                studentChoice.add(rs.getString("name") + " (" + rs.getString("roll_no") + ") ID: " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFees() {
        if (studentChoice.getSelectedIndex() == -1) return;
        String studentItem = studentChoice.getSelectedItem();
        int studentId = Integer.parseInt(studentItem.split("ID: ")[1]);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM fees WHERE student_id = ?")) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tuitionField.setText(String.valueOf(rs.getDouble("tuition_fee")));
                hostelField.setText(String.valueOf(rs.getDouble("hostel_fee")));
                scholarshipField.setText(String.valueOf(rs.getDouble("scholarship")));
                calculateTotal();
            } else {
                tuitionField.setText("");
                hostelField.setText("");
                scholarshipField.setText("");
                totalField.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calculateTotal() {
        try {
            double tuition = tuitionField.getText().isEmpty() ? 0 : Double.parseDouble(tuitionField.getText());
            double hostel = hostelField.getText().isEmpty() ? 0 : Double.parseDouble(hostelField.getText());
            double scholarship = scholarshipField.getText().isEmpty() ? 0 : Double.parseDouble(scholarshipField.getText());
            double total = tuition + hostel - scholarship;
            totalField.setText(String.valueOf(total));
        } catch (NumberFormatException e) {
            totalField.setText("Invalid input");
        }
    }

    private void saveFees() {
        if (studentChoice.getSelectedIndex() == -1) {
            messageLabel.setText("Select a student.");
            return;
        }
        String studentItem = studentChoice.getSelectedItem();
        int studentId = Integer.parseInt(studentItem.split("ID: ")[1]);
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            double tuition = Double.parseDouble(tuitionField.getText());
            double hostel = Double.parseDouble(hostelField.getText());
            double scholarship = Double.parseDouble(scholarshipField.getText());
            conn = DBConnection.getConnection();
            if (feesExist(studentId)) {
                pstmt = conn.prepareStatement("UPDATE fees SET tuition_fee = ?, hostel_fee = ?, scholarship = ? WHERE student_id = ?");
                pstmt.setDouble(1, tuition);
                pstmt.setDouble(2, hostel);
                pstmt.setDouble(3, scholarship);
                pstmt.setInt(4, studentId);
            } else {
                pstmt = conn.prepareStatement("INSERT INTO fees (student_id, tuition_fee, hostel_fee, scholarship) VALUES (?, ?, ?, ?)");
                pstmt.setInt(1, studentId);
                pstmt.setDouble(2, tuition);
                pstmt.setDouble(3, hostel);
                pstmt.setDouble(4, scholarship);
            }
            if (pstmt.executeUpdate() > 0) {
                messageLabel.setText("Fees saved.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Enter valid numbers.");
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

    private boolean feesExist(int studentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM fees WHERE student_id = ?")) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    private void printReceipt() {
        if (studentChoice.getSelectedIndex() == -1 || totalField.getText().isEmpty()) {
            messageLabel.setText("Calculate total first.");
            return;
        }
        String studentItem = studentChoice.getSelectedItem();
        String receipt = "Student: " + studentItem + "\n" +
                         "Tuition: " + tuitionField.getText() + "\n" +
                         "Hostel: " + hostelField.getText() + "\n" +
                         "Scholarship: " + scholarshipField.getText() + "\n" +
                         "Total: " + totalField.getText();
        // For simplicity, print to console or save to file
        try (PrintWriter pw = new PrintWriter("receipt.txt")) {
            pw.println(receipt);
            messageLabel.setText("Receipt saved to receipt.txt");
        } catch (IOException e) {
            messageLabel.setText("Error saving receipt.");
        }
    }
}
