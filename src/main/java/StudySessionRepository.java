import java.sql.*;
import java.util.ArrayList;

public class StudySessionRepository {
    //Replace future incomplete sessions atomically

    public void saveTimetable(long studentID,ArrayList<StudySession> sessions)
    throws SQLException{

        String deleteSQL = """
        DELETE FROM study_sessions
        WHERE student_id = ?
        AND completed = FALSE
        AND study_date >= CURRENT_DATE()
        """;

        String insertSQL = """
        INSERT INTO study_sessions
        (student_id,chapter_id,study_date,
        start_time, end_time, completed)
        SELECT ?,c.id, ?,?,?,FALSE
        FROM chapters c
        JOIN modules m ON c.module_id = m.id
        WHERE c.id = ? AND m.student_id = ?
        """;

        try(Connection connection = DatabaseConnection.getConnection()){

            connection.setAutoCommit(false);
            try{
                try(PreparedStatement delete = connection.prepareStatement(deleteSQL)){
                    delete.setLong(1,studentID);
                    delete.executeUpdate();
                }
                try(PreparedStatement insert = connection.prepareStatement(insertSQL)){
                    for(StudySession session:sessions){
                        insert.setLong(1,studentID);
                        insert.setDate(2,Date.valueOf(session.getDate()));
                        insert.setTime(3,Time.valueOf(session.getStartTime()));
                        insert.setTime(4,Time.valueOf(session.getEndTime()));
                        insert.setLong(5,session.getChapter().getID());
                        insert.setLong(6,studentID);

                        if(insert.executeUpdate() !=1){
                            throw new SQLException(
                                "Chapter does not belong to student: "+ session.getChapter().getID()
                            );
                        }
                    }
                }
                connection.commit();
            }catch(SQLException | RuntimeException e){
                try{
                    connection.rollback();
                }catch(SQLException rollbackError){
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    //Retrieve saved sessions belonging to the student.
    public ArrayList<StudySession> findByStudent(long studentID)
    throws SQLException{
        ArrayList<StudySession> sessions = new ArrayList<>();

        String sql = """
        SELECT s.id, s.study_date,s.start_time,
        s.end_time, s.completed,
        c.id AS chapter_id,c.name,
        c.difficulty, c.confidence,
        c.completed AS chapter_completed
        FROM study_sessions s
        JOIN chapters c ON s.chapter_id = c.id
        JOIN modules m ON c.module_id = m.id
        WHERE s.student_id = ?
        AND m.student_id = ?
        ORDER BY s.study_date, s.start_time, s.id
        """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setLong(1,studentID);
                statement.setLong(2,studentID);

                try(ResultSet results = statement.executeQuery()){
                    while(results.next()){
                        Chapter chapter = new Chapter(
                            results.getString("name"),
                            results.getInt("difficulty"),
                            results.getDouble("confidence"));
                        
                        chapter.setID(results.getLong("chapter_id"));

                        if(results.getBoolean("chapter_completed")){
                            chapter.setChapterComplete();
                        }

                        StudySession session = new StudySession(chapter,
                            results.getDate("study_date").toLocalDate(),
                            results.getTime("start_time").toLocalTime(),
                            results.getTime("end_time").toLocalTime());

                        session.setID(results.getLong("id"));

                        if(results.getBoolean("completed")){
                            session.setComplete();
                        }

                        sessions.add(session);
                    }
                }
            }
            return sessions;
    }

    
    public boolean markCompleted(long sessionID, long studentID)
        throws SQLException {

        String sql = """
        UPDATE study_sessions
        SET completed = TRUE
        WHERE id = ?
          AND student_id = ?
          AND completed = FALSE
        """;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, sessionID);
            statement.setLong(2, studentID);

            return statement.executeUpdate() == 1;
    }
}

}
