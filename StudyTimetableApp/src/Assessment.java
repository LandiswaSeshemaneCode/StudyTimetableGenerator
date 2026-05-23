import java.time.LocalDate;

public class Assessment {
    private String assessmentName;
    private LocalDate assessmentDate;
    private double goalConfidence;
    private LLChapterNode testedChapterHead;
    private LLChapterNode testedChapterTail;
    private Module module;

    public Assessment(String assessmentName,LocalDate assessmentDate,double goalConfidence,Module module){
        this.assessmentName = assessmentName;
        this.assessmentDate = assessmentDate;
        this.goalConfidence = goalConfidence;
        this.module = module;
    }

    public LocalDate getAssessmentDate(){
        return assessmentDate;
    }


    public void addAssessedChapter(Chapter chapter){
        LLChapterNode addNode = new LLChapterNode(chapter);

        if(testedChapterHead == null){
            testedChapterHead = addNode;
            testedChapterTail = addNode;
        }
        else{
            testedChapterTail.next = addNode;
            testedChapterTail = addNode;
        }

    }


}
