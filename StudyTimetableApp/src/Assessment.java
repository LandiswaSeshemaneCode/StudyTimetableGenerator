import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.temporal.ChronoUnit;

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

    public long getDaysUntilAssessment(){
        return ChronoUnit.DAYS.between(LocalDate.now(), assessmentDate);
        //counting number of days until the assessment
    }

    public void displayStudyReccomendation(){
        ArrayList<Chapter> priorityChapters = new ArrayList<>();
        LLChapterNode curHead = testedChapterHead;
        while(curHead!=null){
            priorityChapters.add(curHead.cargo);
            curHead = curHead.next;
        }
        priorityChapters.sort(Comparator.comparingDouble(Chapter::getPriorityScore).reversed());
         //reversing so that bigger scores come first
        System.out.println("Recommended Study Order: ");
         for(int i = 0;i<priorityChapters.size();i++){
            System.out.println((i+1)+ ". " +priorityChapters.get(i).getChapterName() + " | Priority Score: "+priorityChapters.get(i).getPriorityScore());
         }
        }

    public void displayAssessment(){
        System.out.println("Assessment: "+assessmentName);
        System.out.println("Date: "+assessmentDate);
        System.out.println(getDaysUntilAssessment());
        System.out.println("Goal Confidence: "+goalConfidence);
        System.out.println("Module: "+module.getModuleName());

        System.out.println("Tested Chapters: ");
        LLChapterNode current = testedChapterHead;

        while(current!=null){
            System.out.println("- "+current.cargo.getChapterName() + " | Priority Score: "+current.cargo.getPriorityScore());
            current = current.next;
        }

        displayStudyReccomendation();
    }


}
