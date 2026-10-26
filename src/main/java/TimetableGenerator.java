import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalTime;
import java.util.PriorityQueue;
import java.util.Comparator;
public class TimetableGenerator {
    private ArrayList<StudySession> sessions;
    private ArrayList<StudyRecommendation> failedRecommendations;
    private ArrayList<StudyRecommendation> lateRecommendations;
    private PriorityQueue<StudyRecommendation> recommendationsQueue;

    public TimetableGenerator(){
        sessions = new ArrayList<>();
        failedRecommendations = new ArrayList<StudyRecommendation>();
        lateRecommendations = new ArrayList<StudyRecommendation>();
        recommendationsQueue = new PriorityQueue<StudyRecommendation>(
            Comparator.comparing(StudyRecommendation::earliestDate).
        thenComparing(Comparator.comparingDouble(StudyRecommendation::getFinalScore).reversed()));
    }

    public void generateTimetable(
    ArrayList<StudyRecommendation> recommendations,
    ArrayList<Availability> availability,
    int daysAhead,
    ArrayList<StudySession> completedSessions){
        sessions.clear();
        recommendationsQueue.clear();
        failedRecommendations.clear();
        lateRecommendations.clear();
        for(int k = 0;k<recommendations.size();k++){
            if(recommendations.get(k).getRemainingStudyHours()>0){
            recommendationsQueue.add(recommendations.get(k));
            }
        }
        
        LocalDate startDate = LocalDate.now();
        ArrayList<StudySlot> studySlots =
        generateAvailableSlots(
                availability,
                startDate,
                daysAhead,
                completedSessions
        );
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
        else{
            lateRecommendations.add(recommendation);
        }
        
    }
   }
    while(!recommendationsQueue.isEmpty()){
        if(recommendationsQueue.peek().getRemainingStudyHours()>0){
            failedRecommendations.add(recommendationsQueue.poll());
        }
    }

    }

    //here 
    
public ArrayList<StudySlot> generateAvailableSlots(
        ArrayList<Availability> availability,
        LocalDate startDate,
        int daysAhead,
        ArrayList<StudySession> completedSessions) {

    ArrayList<StudySlot> availableSlots = new ArrayList<>();

    for (int i = 0; i < daysAhead; i++) {
        LocalDate currentDate = startDate.plusDays(i);

        for (Availability entry : availability) {

            if (entry.getDayOfWeek() != currentDate.getDayOfWeek()) {
                continue;
            }

            ArrayList<StudySlot> remainingSlots = new ArrayList<>();

            remainingSlots.add(new StudySlot(
                    currentDate,
                    entry.getStartTime(),
                    entry.getEndTime()
            ));

            for (StudySession completed : completedSessions) {

                if (!completed.isCompleted()
                        || !completed.getDate().equals(currentDate)) {
                    continue;
                }

                ArrayList<StudySlot> updatedSlots = new ArrayList<>();

                for (StudySlot slot : remainingSlots) {
                    updatedSlots.addAll(
                            subtractCompletedSession(slot, completed)
                    );
                }

                remainingSlots = updatedSlots;
            }

            availableSlots.addAll(remainingSlots);
        }
    }

    availableSlots.sort(
            Comparator.comparing(StudySlot::getSlotDate)
                    .thenComparing(StudySlot::getSTime)
    );

    return availableSlots;
}

            private ArrayList<StudySlot> subtractCompletedSession(
                    StudySlot slot,
                    StudySession completed) {

                ArrayList<StudySlot> result = new ArrayList<>();

                LocalTime slotStart = slot.getSTime();
                LocalTime slotEnd = slot.getETime();

                LocalTime completedStart = completed.getStartTime();
                LocalTime completedEnd = completed.getEndTime();

                // No overlap: retain the original slot.
                if (!slotStart.isBefore(completedEnd)
                        || !completedStart.isBefore(slotEnd)) {
                    result.add(slot);
                    return result;
                }

                // Available time before the completed session.
                if (slotStart.isBefore(completedStart)) {
                    result.add(new StudySlot(
                            slot.getSlotDate(),
                            slotStart,
                            completedStart
                    ));
                }

                // Available time after the completed session.
                if (completedEnd.isBefore(slotEnd)) {
                    result.add(new StudySlot(
                            slot.getSlotDate(),
                            completedEnd,
                            slotEnd
                    ));
                }

                return result;
            }


                public ArrayList<StudyRecommendation> getFailedRecommendations(){
                    return failedRecommendations;
                }

                public ArrayList<StudySession> getSessions() {
                return sessions;
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
