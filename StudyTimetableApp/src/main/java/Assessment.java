import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.temporal.ChronoUnit;

public class Assessment implements Serializable{
    private String assessmentName;
    private LocalDate assessmentDate;
    private double goalConfidence;
    private LLChapterNode testedChapterHead;
    private LLChapterNode testedChapterTail;
    @JsonIgnore 
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

    public Module getModule(){
        return module;
    }

    public String getAssessmentName(){
        return assessmentName;
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

    public ArrayList<Chapter> getAssessedChapters(){
        LLChapterNode temporary = testedChapterHead;
        ArrayList<Chapter> assessed = new ArrayList<>();

        while(temporary!=null){
            assessed.add(temporary.cargo);
            temporary = temporary.next;
        }

        return assessed;
    }

    public long getDaysUntilAssessment(){
        return ChronoUnit.DAYS.between(LocalDate.now(), assessmentDate);
        //counting number of days until the assessment
    }

    public long urgencyScore(){
        long daysLeft = getDaysUntilAssessment();
        if(daysLeft>=30){
            return 0;
            //Far away not as urgent
        }
        if(daysLeft<0){
            return 0;
            //The assessment date has passed
        }

        return (30-daysLeft)*5;
    }

    public void displayStudyRecommendation(){
        ArrayList<Chapter> priorityChapters = getAssessedChapters();
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
        System.out.println("Goal Confidence: "+goalConfidence);
        System.out.println("Module: "+module.getModuleName());

        System.out.println("Tested Chapters: ");
        LLChapterNode current = testedChapterHead;

        while(current!=null){
            System.out.println("- "+current.cargo.getChapterName() + " | Priority Score: "+current.cargo.getPriorityScore());
            current = current.next;
        }

       // displayStudyRecommendation();
    }


}
