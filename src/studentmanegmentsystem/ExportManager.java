package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;

/**
 * Export data to .txt file.
 */
public class ExportManager extends Frame implements ActionListener {
    private Button exportBtn, backBtn;
    private Label messageLabel;

    public ExportManager() {
        setTitle("Export Data");
        setSize(300, 200);
        setLayout(new GridLayout(3, 1));
        setLocationRelativeTo(null);

        exportBtn = new Button("Export Students to .txt");
        backBtn = new Button("Back");
        messageLabel = new Label("");
        messageLabel.setForeground(Color.BLUE);

        exportBtn.addActionListener(this);
        backBtn.addActionListener(this);

        add(exportBtn);
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
        if (ae.getSource() == exportBtn) {
            exportData();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void exportData() {
        FileDialog fd = new FileDialog(this, "Save File", FileDialog.SAVE);
        fd.setFile("students.txt");
        fd.setVisible(true);
        String dir = fd.getDirectory();
        String file = fd.getFile();
        if (dir == null || file == null) return;

        String path = dir + file;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        FileWriter fw = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM students");
            fw = new FileWriter(path);
            fw.write("ID,Name,Roll No,Branch,Age\n");
            while (rs.next()) {
                fw.write(rs.getInt("id") + "," + rs.getString("name") + "," +
                        rs.getString("roll_no") + "," + rs.getString("branch") + "," +
                        rs.getInt("age") + "\n");
            }
            messageLabel.setText("Data exported to " + path);
        } catch (SQLException | IOException e) {
            messageLabel.setText("Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                if (fw != null) fw.close();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
