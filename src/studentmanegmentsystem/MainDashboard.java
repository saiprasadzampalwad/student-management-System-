package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;

/**
 * Main Dashboard for navigating to different modules.
 */
public class MainDashboard extends Frame implements ActionListener {
    private Button addStudentBtn, viewStudentsBtn, updateStudentBtn, deleteStudentBtn;
    private Button courseManagerBtn, marksEntryBtn, feeManagerBtn, searchFilterBtn, analyticsBtn;

    public MainDashboard() {
        setTitle("Dashboard - Student Management System");
        setSize(600, 400);
        setLayout(new GridLayout(3, 3));
        setLocationRelativeTo(null);

        // Initialize buttons
        addStudentBtn = new Button("Add Student");
        viewStudentsBtn = new Button("View Students");
        updateStudentBtn = new Button("Update Student");
        deleteStudentBtn = new Button("Delete Student");
        courseManagerBtn = new Button("Course Manager");
        marksEntryBtn = new Button("Marks Entry");
        feeManagerBtn = new Button("Fee Manager");
        searchFilterBtn = new Button("Search & Filter");
        analyticsBtn = new Button("Analytics");

        // Add action listeners
        addStudentBtn.addActionListener(this);
        viewStudentsBtn.addActionListener(this);
        updateStudentBtn.addActionListener(this);
        deleteStudentBtn.addActionListener(this);
        courseManagerBtn.addActionListener(this);
        marksEntryBtn.addActionListener(this);
        feeManagerBtn.addActionListener(this);
        searchFilterBtn.addActionListener(this);
        analyticsBtn.addActionListener(this);

        // Add buttons to frame
        add(addStudentBtn);
        add(viewStudentsBtn);
        add(updateStudentBtn);
        add(deleteStudentBtn);
        add(courseManagerBtn);
        add(marksEntryBtn);
        add(feeManagerBtn);
        add(searchFilterBtn);
        add(analyticsBtn);

        // Window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                DBConnection.closeConnection();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addStudentBtn) {
            new AddStudent();
        } else if (ae.getSource() == viewStudentsBtn) {
            new ViewStudents();
        } else if (ae.getSource() == updateStudentBtn) {
            new UpdateStudent();
        } else if (ae.getSource() == deleteStudentBtn) {
            new DeleteStudent();
        } else if (ae.getSource() == courseManagerBtn) {
            new CourseManager();
        } else if (ae.getSource() == marksEntryBtn) {
            new MarksEntry();
        } else if (ae.getSource() == feeManagerBtn) {
            new FeeManager();
        } else if (ae.getSource() == searchFilterBtn) {
            new SearchFilter();
        } else if (ae.getSource() == analyticsBtn) {
            new AnalyticsDashboard();
        }
    }
}
