import java.time.LocalDate;

public class StudyPlanService{


    public Module createModule(String mName){
       return new Module(mName);
    }

    public Chapter createChapter(String cName,int cDifficulty,double cConfidence){
        return new Chapter(cName,cDifficulty,cConfidence);
    }

    public Assessment createAssessment(String aName,LocalDate aDate,double aGoal,Module mod){
     return new Assessment(aName, aDate, aGoal, mod);
    }

     public void addAssessedChapter(Chapter chapter,Assessment assessment){ 
        assessment.addAssessedChapter(chapter);
    }

    public void addChapter(Module mod,Chapter chapter){
        mod.addChapter(chapter);
    }
}