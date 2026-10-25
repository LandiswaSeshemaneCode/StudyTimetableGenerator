import java.sql.*;
import java.util.ArrayList;

public class ChapterRepository {

    public long save(long moduleID, String name,
        int difficulty, double confidence)throws SQLException{

            String sql = """
            INSERT INTO chapters
            (module_id,name,difficulty,confidence)
            VALUES (?,?,?,?)
            """;

            try(Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){

                statement.setLong(1,moduleID);
                statement.setString(2,name);
                statement.setInt(3,difficulty);
                statement.setDouble(4, confidence);

                statement.executeUpdate();

                try(ResultSet keys = statement.getGeneratedKeys()){
                    if(keys.next()){
                        return keys.getLong(1);
                    }
                }
                throw new SQLException("Chapter ID was not generated.");
                }

    }

    public ArrayList<Chapter> findByModule(long moduleID) throws SQLException{

        ArrayList<Chapter> chapters = new ArrayList<>();

        String sql = """
        SELECT id, name, difficulty, confidence, completed
        FROM chapters
        WHERE module_id = ?
        ORDER BY id
        """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            
            statement.setLong(1, moduleID);

            try(ResultSet results = statement.executeQuery()){
                while(results.next()){
                    Chapter chapter = new Chapter(results.getString("name"),
                                        results.getInt("difficulty"),
                                        results.getDouble("confidence"));

                    chapter.setID(results.getLong("id"));
                    
                    if(results.getBoolean("completed")){
                        chapter.setChapterComplete();
                    }

                    chapters.add(chapter);
                }
            }
            
        }

        return chapters;
    }

}
