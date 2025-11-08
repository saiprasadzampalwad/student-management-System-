package studentmanegmentsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Sign Up page for the Student Management System.
 */
public class SignUpPage extends Frame implements ActionListener {
    private TextField usernameField, passwordField, confirmPasswordField;
    private Button signUpButton;
    private Label messageLabel;

    public SignUpPage() {
        setTitle("Sign Up - Student Management System");
        setSize(400, 350);
        setLayout(new GridLayout(6, 2));
        setLocationRelativeTo(null); // Center the frame

        // Components
        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordField = new TextField();
        passwordField.setEchoChar('*');

        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordField = new TextField();
        confirmPasswordField.setEchoChar('*');

        signUpButton = new Button("Sign Up");
        signUpButton.addActionListener(this);

        messageLabel = new Label("");
        messageLabel.setForeground(Color.RED);

        // Add components
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(confirmPasswordLabel);
        add(confirmPasswordField);
        add(new Label("")); // Empty cell
        add(signUpButton);
        add(new Label("")); // Empty cell
        add(messageLabel);

        // Window listener to close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == signUpButton) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username and password cannot be empty.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match.");
                return;
            }

            if (registerUser(username, password)) {
                messageLabel.setText("Sign up successful! You can now login.");
                // Optionally, go back to login
                dispose();
            } else {
                messageLabel.setText("Username already exists or error occurred.");
            }
        }
    }

    private boolean registerUser(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int rows = pstmt.executeUpdate();
            return rows > 0;
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
