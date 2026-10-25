import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class AssessmentRepository {
    

    public long save(long moduleID,String name,LocalDate date,double goalConfidence)throws SQLException{ 
    
     String sql = """
        INSERT INTO assessments
        (module_id,name,assessment_date,goal_confidence)
        VALUES (?,?,?,?)
    """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){

                statement.setLong(1,moduleID);
                statement.setString(2, name);
                statement.setDate(3, Date.valueOf(date));
                statement.setDouble(4, goalConfidence);

                statement.executeUpdate();

                try(ResultSet keys = statement.getGeneratedKeys()){

                    if(keys.next()){
                        return keys.getLong(1);
                    }
                }

                throw new SQLException("Assessment ID was not generated.");
            }
    
    }

    public ArrayList<Assessment> findByModule(long moduleID, Module module) throws SQLException{

        ArrayList<Assessment> assessments = new ArrayList<>();

        String sql = """
        SELECT id, name, assessment_date, goal_confidence
        FROM assessments
        WHERE module_id = ?
        ORDER BY assessment_date
        """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

                statement.setLong(1,moduleID);

                try(ResultSet results = statement.executeQuery()){
                    while(results.next()){
                        Assessment assessment = new Assessment(results.getString("name"),
                         results.getDate("assessment_date").toLocalDate(), results.getDouble("goal_confidence"), module);

                         assessment.setID(results.getLong("id"));
                         assessments.add(assessment);
                    }
                }
            }
            return assessments;
    }

    public Assessment findByID(long assessmentID,long moduleID,Module module)throws SQLException{

        String sql = """
        SELECT id,name,assessment_date,goal_confidence
        FROM assessments
        WHERE id = ? and module_id = ?
        """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setLong(1,assessmentID);
                statement.setLong(2,moduleID);

                try(ResultSet results = statement.executeQuery()){

                    if(results.next()){
                        Assessment assessment = new Assessment(results.getString("name"),
                         results.getDate("assessment_date").toLocalDate(),results.getDouble("goal_confidence"), module);

                         assessment.setID(results.getLong("id"));
                         return assessment;
                    }
                }
            }
            return null;
    }
}
