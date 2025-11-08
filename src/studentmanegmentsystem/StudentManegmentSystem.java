/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package studentmanegmentsystem;

/**
 *
 * @author HP
 */
public class StudentManegmentSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Initialize database connection
        try {
            DBConnection.getConnection();
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        // Launch Login Page
        new LoginPage();
    }
    
}
