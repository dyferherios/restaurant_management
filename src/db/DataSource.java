package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private final static int port = 5432;
    private final String user = System.getenv("DB_USER");
    private final String pwd = System.getenv("DB_PASSWORD");
    private final String jdbcUrl;
    private final String url = System.getenv("DB_URL");

    public DataSource(){
        String host = "localhost:";
        String db = "restaurant_management";
        jdbcUrl = url + host + port + "/" + db;
    }

    public Connection getConnection(){
        try {
            return DriverManager.getConnection(jdbcUrl, user, pwd);
        } catch (SQLException e) {
            throw new RuntimeException("error : "+ e.getMessage(), e);
        }
    }
}