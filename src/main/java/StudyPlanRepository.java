import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;


public class StudyPlanRepository {
    
    private final ModuleRepository moduleRepository = new ModuleRepository();
    private final ChapterRepository chapterRepository = new ChapterRepository();
    private final AssessmentRepository assessmentRepository = new AssessmentRepository();
    private final AssessedChapterRepository assessedChapterRepository = new AssessedChapterRepository();

    public ArrayList<Module> loadStudyPlan(long studentID) throws SQLException{

        ArrayList<Module> modules = moduleRepository.findByStudent(studentID);

        for(Module module:modules){
            long moduleID = module.getID();

            ArrayList<Chapter> chapters = chapterRepository.findByModule(moduleID);

            Map<Long, Chapter> chaptersByID = new HashMap<>();

            for(Chapter chapter: chapters){
                module.addChapter(chapter);
                chaptersByID.put(chapter.getID(),chapter);
            }

            ArrayList<Assessment> assessments = assessmentRepository.findByModule(moduleID, module);

            for(Assessment assessment:assessments){
                module.addAssessment(assessment);
                ArrayList<Long> chapterIDs = assessedChapterRepository.findByAssessment(assessment.getID());

                for(Long chapterID:chapterIDs){
                    Chapter chapter = chaptersByID.get(chapterID);
                    if(chapter==null){
                        throw new SQLException("Assessment references a chapter "+"ouside its module: "+chapterID);
                    }
                    assessment.addAssessedChapter(chapter);
                }
            }
        }
        return modules;
    }
    
}
