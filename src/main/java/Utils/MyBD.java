package Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyBD {
    private static MyBD instance;
    private Connection conn;

    private final String URL = "jdbc:mysql://localhost:3307/investdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String USER = "root";
    private final String PASS = "";

    private MyBD() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Connected to DB");
        } catch (SQLException e) {
            System.out.println("❌ DB connection error: " + e.getMessage());
        }
    }

    public static MyBD getInstance() {
        if (instance == null) {
            instance = new MyBD();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }
}
