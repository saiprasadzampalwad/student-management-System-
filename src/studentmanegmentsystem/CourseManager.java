package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 * Manage courses and subjects.
 */
public class CourseManager extends Frame implements ActionListener {
    private TextField courseNameField, subjectNameField;
    private Choice semesterChoice, courseChoice;
    private List subjectList;
    private Button addCourseBtn, addSubjectBtn, removeSubjectBtn, backBtn;
    private Label messageLabel;

    public CourseManager() {
        setTitle("Course & Subject Manager");
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // North panel for courses
        Panel coursePanel = new Panel(new GridLayout(3, 2));
        coursePanel.add(new Label("Course Name:"));
        courseNameField = new TextField();
        coursePanel.add(courseNameField);
        coursePanel.add(new Label("Semester:"));
        semesterChoice = new Choice();
        for (int i = 1; i <= 8; i++) semesterChoice.add(String.valueOf(i));
        coursePanel.add(semesterChoice);
        addCourseBtn = new Button("Add Course");
        coursePanel.add(addCourseBtn);
        coursePanel.add(new Label(""));

        // Center panel for subjects
        Panel subjectPanel = new Panel(new BorderLayout());
        Panel topSubjectPanel = new Panel(new GridLayout(2, 2));
        topSubjectPanel.add(new Label("Select Course:"));
        courseChoice = new Choice();
        loadCourses();
        topSubjectPanel.add(courseChoice);
        topSubjectPanel.add(new Label("Subject Name:"));
        subjectNameField = new TextField();
        topSubjectPanel.add(subjectNameField);

        subjectList = new List();
        subjectList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                // Optional: handle selection
            }
        });

        Panel buttonPanel = new Panel();
        addSubjectBtn = new Button("Add Subject");
        removeSubjectBtn = new Button("Remove Subject");
        buttonPanel.add(addSubjectBtn);
        buttonPanel.add(removeSubjectBtn);

        subjectPanel.add(topSubjectPanel, BorderLayout.NORTH);
        subjectPanel.add(subjectList, BorderLayout.CENTER);
        subjectPanel.add(buttonPanel, BorderLayout.SOUTH);

        // South panel
        Panel southPanel = new Panel(new FlowLayout());
        backBtn = new Button("Back");
        messageLabel = new Label("");
        messageLabel.setForeground(Color.BLUE);
        southPanel.add(backBtn);
        southPanel.add(messageLabel);

        // Add listeners
        addCourseBtn.addActionListener(this);
        addSubjectBtn.addActionListener(this);
        removeSubjectBtn.addActionListener(this);
        backBtn.addActionListener(this);
        courseChoice.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                loadSubjects();
            }
        });

        // Add to frame
        add(coursePanel, BorderLayout.NORTH);
        add(subjectPanel, BorderLayout.CENTER);
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
        if (ae.getSource() == addCourseBtn) {
            addCourse();
        } else if (ae.getSource() == addSubjectBtn) {
            addSubject();
        } else if (ae.getSource() == removeSubjectBtn) {
            removeSubject();
        } else if (ae.getSource() == backBtn) {
            dispose();
        }
    }

    private void addCourse() {
        String name = courseNameField.getText().trim();
        String sem = semesterChoice.getSelectedItem();
        if (name.isEmpty()) {
            messageLabel.setText("Enter course name.");
            return;
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO courses (course_name, semester) VALUES (?, ?)");
            pstmt.setString(1, name);
            pstmt.setInt(2, Integer.parseInt(sem));
            if (pstmt.executeUpdate() > 0) {
                messageLabel.setText("Course added.");
                courseNameField.setText("");
                loadCourses();
            }
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addSubject() {
        if (courseChoice.getSelectedIndex() == -1) {
            messageLabel.setText("Select a course.");
            return;
        }
        String subjectName = subjectNameField.getText().trim();
        if (subjectName.isEmpty()) {
            messageLabel.setText("Enter subject name.");
            return;
        }
        String courseItem = courseChoice.getSelectedItem();
        int courseId = Integer.parseInt(courseItem.split("ID: ")[1].replace(")", ""));
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO subjects (course_id, subject_name) VALUES (?, ?)");
            pstmt.setInt(1, courseId);
            pstmt.setString(2, subjectName);
            if (pstmt.executeUpdate() > 0) {
                messageLabel.setText("Subject added.");
                subjectNameField.setText("");
                loadSubjects();
            }
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSubjects() {
        subjectList.removeAll();
        if (courseChoice.getSelectedIndex() == -1) return;
        String courseItem = courseChoice.getSelectedItem();
        int courseId = Integer.parseInt(courseItem.split("ID: ")[1].replace(")", ""));
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("SELECT subject_name FROM subjects WHERE course_id = ?");
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                subjectList.add(rs.getString("subject_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeSubject() {
        String selected = subjectList.getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a subject to remove.");
            return;
        }
        String courseItem = courseChoice.getSelectedItem();
        int courseId = Integer.parseInt(courseItem.split("ID: ")[1].replace(")", ""));
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("DELETE FROM subjects WHERE course_id = ? AND subject_name = ?");
            pstmt.setInt(1, courseId);
            pstmt.setString(2, selected);
            if (pstmt.executeUpdate() > 0) {
                messageLabel.setText("Subject removed.");
                loadSubjects();
            }
        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
