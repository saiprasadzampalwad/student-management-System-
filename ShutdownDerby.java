import java.sql.DriverManager;
import java.sql.SQLException;

public class ShutdownDerby {
    public static void main(String[] args) {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
            System.out.println("Derby shut down normally");
        } catch (SQLException se) {
            if ((se.getErrorCode() == 50000) && ("XJ015".equals(se.getSQLState()))) {
                System.out.println("Derby shut down normally");
            } else {
                System.err.println("Derby did not shut down normally: " + se.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
