import java.sql.*;
import java.util.ArrayList;

public class ModuleRepository {
    
    public long save(String moduleName,long studentID) throws SQLException{

        String sql = """
            INSERT INTO modules (student_id, name)
            VALUES (?, ?)
            """;
        
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){

                statement.setLong(1,studentID);
                statement.setString(2,moduleName);
                statement.executeUpdate();

                try(ResultSet keys = statement.getGeneratedKeys()){
                    if(keys.next()){
                        return keys.getLong(1);
                    }
                }

                throw new SQLException("Module ID was not generated.");
        }

    }

    public ArrayList<Module> findByStudent(long studentID) throws SQLException{

        ArrayList<Module> modules = new ArrayList<>();
        String sql = """
            SELECT id, name
            FROM modules
            WHERE student_id = ?
            ORDER BY id
            """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

                statement.setLong(1,studentID);

                try(ResultSet results = statement.executeQuery()){
                    while(results.next()){
                        Module module = new Module(results.getString("name"));
                        module.setID(results.getLong("id"));
                        modules.add(module);
                    }
                }
            }
            return modules;
    }

    public Module findByID(long moduleID,long studentID) 
    throws SQLException{

       String sql = """
                SELECT id, name
                FROM modules
                WHERE id = ? AND student_id = ?          
       """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

                statement.setLong(1,moduleID);
                statement.setLong(2,studentID);

                try(ResultSet results = statement.executeQuery()){

                    if(results.next()){
                        Module module = new Module(results.getString("name"));
                        module.setID(results.getLong("id"));
                        return module;
                    }
                }

            }

            return null;
    }
}
