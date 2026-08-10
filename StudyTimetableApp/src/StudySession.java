import java.time.LocalDate;
import java.time.LocalTime;

public class StudySession {
    private Chapter chapter;
    private LocalDate studyDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean completed;

    public StudySession(Chapter chapter,LocalDate studyDate,LocalTime startTime,LocalTime endTime){
        this.chapter = chapter;
        this.studyDate = studyDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.completed = false;
    }

    public Chapter getChapter(){
        return chapter;
    }

    protected LocalDate getDate(){
        return studyDate;
    }

    public LocalTime getStartTime(){
        return startTime;
    }

    public LocalTime getEndTime(){
        return endTime;
    }
    

    public boolean isCompleted(){
        return completed;
    }

    public void setComplete(){
        completed = true;
    }

    public void displaySession(){
        System.out.println("Date: "+studyDate);
        System.out.println("Chapter: "+chapter.getChapterName());
        System.out.println("Time: "+startTime+" - "+endTime);
        System.out.println("Completed: "+completed);

    }
}
