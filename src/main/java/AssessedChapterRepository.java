import java.sql.*;
import java.util.ArrayList;

public class AssessedChapterRepository {
    
    public boolean save(long assessmentID,long chapterID) throws SQLException{

        String sql = """
        INSERT INTO assessment_chapters
        (assessment_id,chapter_id)
        SELECT a.id,c.id
        FROM assessments a
        JOIN chapters c ON c.module_id = a.module_id
        WHERE a.id = ? AND c.id = ?
        """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

                statement.setLong(1,assessmentID);
                statement.setLong(2,chapterID);

                return statement.executeUpdate() == 1;
            }
    }

    public ArrayList<Long> findByAssessment(long assessmentID) throws SQLException{

        ArrayList<Long> chapterIDs = new ArrayList<>();
        String sql = """
        SELECT chapter_id
        FROM assessment_chapters
        WHERE assessment_id = ?
        ORDER BY chapter_id
        """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

                statement.setLong(1,assessmentID);

                try(ResultSet results = statement.executeQuery()){
                    while(results.next()){
                        chapterIDs.add(results.getLong("chapter_id"));
                    }
                }
            }

            return chapterIDs;
    } 
}
