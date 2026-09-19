import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalTime;
import java.util.PriorityQueue;
import java.util.Comparator;
public class TimetableGenerator {
    private ArrayList<StudySession> sessions;
    private ArrayList<StudyRecommendation> failedRecommendations;
    private PriorityQueue<StudyRecommendation> recommendationsQueue;

    public TimetableGenerator(){
        sessions = new ArrayList<>();
        failedRecommendations = new ArrayList<>();
        recommendationsQueue = new PriorityQueue<>(
            Comparator.comparing(StudyRecommendation::earliestDate).
        thenComparing(Comparator.comparingDouble(StudyRecommendation::getFinalScore).reversed()));
    }

    public void generateTimetable(ArrayList<StudyRecommendation> recommendations,ArrayList<Availability> availability,int daysAhead){
        sessions.clear();
        recommendationsQueue.clear();
        failedRecommendations.clear();
        for(int k = 0;k<recommendations.size();k++){
            if(recommendations.get(k).getRemainingStudyHours()>0){
            recommendationsQueue.add(recommendations.get(k));
            }
        }
        
        LocalDate startDate = LocalDate.now();
        ArrayList<StudySlot> studySlots = generateAvailableSlots(availability,startDate,daysAhead);
        for(StudySlot slot : studySlots){  
             double slotRemaining  = slot.getDurationHours();
             LocalTime currentStartTime = slot.getSTime();
            while(slotRemaining>0 && !recommendationsQueue.isEmpty()){
            StudyRecommendation recommendation = recommendationsQueue.poll();
            if(!slot.getSlotDate().isAfter(recommendation.earliestDate())){
            double allocatedHours = Math.min(slotRemaining,recommendation.getRemainingStudyHours());
            LocalTime endTime = currentStartTime.plusMinutes((long)(allocatedHours*60));
            sessions.add(new StudySession(recommendation.getChapter(),slot.getSlotDate(),currentStartTime,endTime));
            recommendation.calculateRemainingHours(allocatedHours);
             slotRemaining-=allocatedHours;
             currentStartTime = endTime;
            if(recommendation.getRemainingStudyHours()>0){
                recommendationsQueue.add(recommendation);
                 
            }
        }  
        
    }
   }
    while(!recommendationsQueue.isEmpty()){
        if(recommendationsQueue.peek().getRemainingStudyHours()>0){
            failedRecommendations.add(recommendationsQueue.poll());
        }
    }

    }


    public ArrayList<StudySlot> generateAvailableSlots(ArrayList<Availability> availability,LocalDate startDate,int daysAhead){
        ArrayList<StudySlot> studySlot = new ArrayList<>();
        for(int i = 0;i<daysAhead;i++){
            LocalDate currentDate = startDate.plusDays(i);
            for(int j=0;j<availability.size();j++){
            if(availability.get(j).getDayOfWeek()==currentDate.getDayOfWeek()){
                LocalTime startTime = availability.get(j).getStartTime();
                LocalTime endTime = availability.get(j).getEndTime();
                studySlot.add(new StudySlot(currentDate, startTime, endTime));
            }
        }
        }
        return studySlot;
    }

    public ArrayList<StudyRecommendation> getFailedRecommendations(){
        return failedRecommendations;
    }

    public void displayTimetable(){
        System.out.println("\nGenerated Timetable");
        System.out.println("----------------------");

        for(int i = 0;i<sessions.size();i++){
            StudySession studySession = sessions.get(i);
            System.out.println("Date: "+studySession.getDate());
            System.out.println("Chapter: "+studySession.getChapter().getChapterName());
            System.out.println("Time: "+studySession.getStartTime()+" - "+studySession.getEndTime());
            System.out.println("\n");
        }

    }
}
