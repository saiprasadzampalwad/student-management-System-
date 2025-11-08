package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Login page for the Student Management System.
 */
public class LoginPage extends Frame implements ActionListener {
    private TextField usernameField, passwordField;
    private Button loginButton, signUpButton;
    private Label messageLabel;

    public LoginPage() {
        setTitle("Login - Student Management System");
        setSize(400, 350);
        setLayout(new GridLayout(6, 2));
        setLocationRelativeTo(null); // Center the frame

        // Components
        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordField = new TextField();
        passwordField.setEchoChar('*');

        loginButton = new Button("Login");
        loginButton.addActionListener(this);

        signUpButton = new Button("Sign Up");
        signUpButton.addActionListener(this);

        messageLabel = new Label("");
        messageLabel.setForeground(Color.RED);

        // Add components
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new Label("")); // Empty cell
        add(loginButton);
        add(new Label("")); // Empty cell
        add(signUpButton);
        add(new Label("")); // Empty cell
        add(messageLabel);

        // Window listener to close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginButton) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (validateLogin(username, password)) {
                messageLabel.setText("Login successful!");
                // Launch MainDashboard
                new MainDashboard();
                dispose(); // Close login window
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        } else if (ae.getSource() == signUpButton) {
            new SignUpPage();
        }
    }

    private boolean validateLogin(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
