public class StudyRecommendation {
    private Chapter chapter;
    private Assessment assessment;
    private double finalScore;

    public StudyRecommendation(Chapter chapter,Assessment assessment){
        this.chapter = chapter;
        this.assessment = assessment;
        this.finalScore = chapter.getPriorityScore() + assessment.urgencyScore();
    }

    public double getFinalScore(){
        return finalScore;
    }

    public void displayRecommendation(int rank){
        System.out.println(rank + ". "+ chapter.getChapterName()+ " | "+assessment.getModule().getModuleName()+" | "+assessment.getAssessmentName()+" | Score: "+ finalScore);
    }
}
