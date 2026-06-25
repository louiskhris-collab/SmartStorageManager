import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class DatabaseManager {

        private static final String URL = "jdbc:mysql://localhost:3306/smart_storage_db";
        private static final String USER = "root";
        private static final String PASSWORD = "Kladenstien!23*";

        public static Connection getConnection() {
            try {
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to database successfully.");
                return connection;
            } catch (SQLException e) {
                System.out.println("Database connection failed.");
                System.out.println(e.getMessage());
                return null;
            }
        }
    }

