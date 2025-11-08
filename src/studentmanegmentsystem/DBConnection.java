package studentmanegmentsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles database connection and schema creation for Apache Derby.
 */
public class DBConnection {

    private static Connection conn = null;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            } catch (ClassNotFoundException e) {
                System.out.println("Could not load JDBC Driver");
                throw new SQLException(e);
            }
            conn = DriverManager.getConnection("jdbc:derby:sample;create=true");
            createTablesIfNotExist();
        }
        return conn;
    }

    private static void createTablesIfNotExist() {
        try (Statement stmt = conn.createStatement()) {
            // Users table for login
            try {
                stmt.executeUpdate("CREATE TABLE users (" +
                        "username VARCHAR(50) PRIMARY KEY, " +
                        "password VARCHAR(50))");
            } catch (SQLException e) {
                // Table already exists
            }

            // Students table
            try {
                stmt.executeUpdate("CREATE TABLE students (" +
                        "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "name VARCHAR(100), " +
                        "roll_no VARCHAR(20) UNIQUE, " +
                        "branch VARCHAR(50), " +
                        "age INT, " +
                        "course_id INT, " +
                        "FOREIGN KEY (course_id) REFERENCES courses(course_id))");
            } catch (SQLException e) {
                // Table already exists, try to alter if column missing
                try {
                    stmt.executeUpdate("ALTER TABLE students ADD COLUMN course_id INT");
                    stmt.executeUpdate("ALTER TABLE students ADD FOREIGN KEY (course_id) REFERENCES courses(course_id)");
                } catch (SQLException alterE) {
                    // Column already exists or alter failed
                }
            }

            // Courses table
            try {
                stmt.executeUpdate("CREATE TABLE courses (" +
                        "course_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "course_name VARCHAR(100), " +
                        "semester INT)");
            } catch (SQLException e) {
                // Table already exists
            }

            // Subjects table
            try {
                stmt.executeUpdate("CREATE TABLE subjects (" +
                        "subject_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "course_id INT, " +
                        "subject_name VARCHAR(100), " +
                        "FOREIGN KEY (course_id) REFERENCES courses(course_id))");
            } catch (SQLException e) {
                // Table already exists
            }

            // Marks table
            try {
                stmt.executeUpdate("CREATE TABLE marks (" +
                        "student_id INT, " +
                        "subject_id INT, " +
                        "marks INT, " +
                        "PRIMARY KEY (student_id, subject_id), " +
                        "FOREIGN KEY (student_id) REFERENCES students(id), " +
                        "FOREIGN KEY (subject_id) REFERENCES subjects(subject_id))");
            } catch (SQLException e) {
                // Table already exists
            }

            // Fees table
            try {
                stmt.executeUpdate("CREATE TABLE fees (" +
                        "student_id INT PRIMARY KEY, " +
                        "tuition_fee DECIMAL(10,2), " +
                        "hostel_fee DECIMAL(10,2), " +
                        "scholarship DECIMAL(10,2), " +
                        "FOREIGN KEY (student_id) REFERENCES students(id))");
            } catch (SQLException e) {
                // Table already exists
            }

            // Insert default admin user
            try {
                stmt.executeUpdate("INSERT INTO users (username, password) VALUES ('admin', 'admin')");
            } catch (SQLException e) {
                // User already exists, ignore
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
