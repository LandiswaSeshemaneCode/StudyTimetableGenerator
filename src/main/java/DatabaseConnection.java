import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLDataException;
import java.sql.SQLException;

public class DatabaseConnection{

    private static final String URL = "jdbc:mysql://localhost:3306/study_timetable";

    public static Connection getConnection() throws SQLException{

        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if(username == null || password == null){
            throw new IllegalStateException(
                "DB_USER and DB_PASSWORD must be configured"
            );
        }

        return DriverManager.getConnection(URL,username,password);
    }

    public static void main(String[] args){
        try(Connection connection = getConnection()){
            System.out.println("System has connected to MySQL successfully.");
        }catch(SQLException | IllegalStateException e){
            System.err.println("Database connection failed: "+e.getMessage());
        }
    }
}