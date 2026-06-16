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
            addNode.prev = testedChapterTail;
            testedChapterTail.next = addNode;
            testedChapterTail = addNode;
        }

    }

    public void displayAssessment(){
        System.out.println("Assessment: "+assessmentName);
        System.out.println("Date: "+assessmentDate);
        System.out.println("Goal Confidence: "+goalConfidence);
        System.out.println("Module: "+module.getModuleName());

        System.out.println("Tested Chapters: ");
        LLChapterNode current = testedChapterHead;

        while(current!=null){
            System.out.println("- "+current.cargo.getChapterName());
            current = current.next;
        }
    }


}
