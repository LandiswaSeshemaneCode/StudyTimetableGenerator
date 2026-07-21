import java.util.ArrayList;
public class StudyRecommendation {
    private Chapter chapter;
    private ArrayList<Assessment> assessments;
    private double finalScore;

    public StudyRecommendation(Chapter chapter){
        this.chapter = chapter;
        assessments = new ArrayList<Assessment>();
        calculateFinalScore();
    }

    private void calculateFinalScore(){
    long highestUrgency = 0;
    for(int i = 0;i<assessments.size();i++){
        long urgency = assessments.get(i).urgencyScore();
        if(urgency>highestUrgency){
            highestUrgency = urgency;
        }
    }

    finalScore = chapter.getPriorityScore() + highestUrgency;
    }

    public void AddAssessment(Assessment assessment){
        assessments.add(assessment);
        calculateFinalScore();
    }
    
    public double getFinalScore(){
        return finalScore;
    }

    public void displayRecommendation(int rank){
        System.out.println(rank + ". "+ chapter.getChapterName()+ " | "+assessments.get(0).getModule().getModuleName()+" | "+assessments.get(0).getAssessmentName()+" | Score: "+ finalScore);

        System.out.print("Assessments: ");
        for(int j=0;j<assessments.size();j++){
            System.out.print(assessments.get(j).getAssessmentName());
            if(j<assessments.size()-1){
                System.out.print(", ");
            }
        }
    }
}
