import java.time.LocalDate;
import java.util.ArrayList;
public class TimetableGenerator {
    private ArrayList<StudySession> sessions;

    public TimetableGenerator(){
        sessions = new ArrayList<StudySession>();
    }

    public void generateTimetable(ArrayList<StudyRecommendation> recommendations,ArrayList<Availability> availability){
        double totalHours = calculateTotalHours(availability);
        double totalScore = calculateTotalScore(recommendations);
        LocalDate studyDate = LocalDate.now();

        for(int i = 0;i<recommendations.size();i++){
            StudyRecommendation studyRecommendation = recommendations.get(i);
            double allocatedHours = ((studyRecommendation.getFinalScore()/totalScore)*totalHours); //later round up to nearest 0.5
            StudySession studySession = new StudySession(studyRecommendation.getChapter(), studyDate, allocatedHours);

            sessions.add(studySession);
        }

    }

    private double calculateTotalHours(ArrayList<Availability> availability){
        double total = 0;
        for(int i = 0;i<availability.size();i++){
            total+=availability.get(i).getAvailableHours();
        }
        return total;
    }

    private double calculateTotalScore(ArrayList<StudyRecommendation> recommendation){
        double total = 0;
        for(int i = 0; i<recommendation.size();i++){
            total+=recommendation.get(i).getFinalScore();
        }
        return total;
    }

    public void displayTimetable(){
        System.out.println("\nGenerated Timetable");
        System.out.println("----------------------");

        for(int i = 0;i<sessions.size();i++){
            StudySession studySession = sessions.get(i);
            System.out.println("Date: "+studySession.getDate());
            System.out.println("Chapter: "+studySession.getChapter().getChapterName());
            System.out.println("Hours: "+studySession.getHours());
            System.out.println("\n");
        }
    }
}
