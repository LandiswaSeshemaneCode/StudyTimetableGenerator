import java.util.ArrayList;
public class StudyRecommendation {
    private Chapter chapter;
    private ArrayList<Assessment> assessments;
    private double finalScore;
    private double requiredStudyHours;
    private double remainingStudyHours;

    public StudyRecommendation(Chapter chapter){
        this.chapter = chapter;
        assessments = new ArrayList<Assessment>();
        calculateFinalScore();
        this.requiredStudyHours = determineStudyHours();
        this.remainingStudyHours = this.requiredStudyHours;
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
        requiredStudyHours = determineStudyHours();
        remainingStudyHours = requiredStudyHours;
    }
    
    public double getFinalScore(){
        return finalScore;
    }

    public Chapter getChapter(){
        return chapter;
    }

    public void calculateRemainingHours(double hours){
     remainingStudyHours = remainingStudyHours - hours;
     if(remainingStudyHours < 0){
        remainingStudyHours = 0;
     }
    }

    public double getRemainingStudyHours(){
        return remainingStudyHours;
    }

    public double determineStudyHours(){
        double studyHours = 0;
        if(finalScore < 80){
        studyHours = 0.5;
        }
        else if(finalScore < 110){
            studyHours = 1;
        }
        else if(finalScore <130){
            studyHours = 1.5;
        }
        else if(finalScore <150){
            studyHours = 2;
        }
        else if(finalScore <175){
            studyHours = 2.5;
        }
        else{
            studyHours = 3;
        }

        return studyHours;
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
