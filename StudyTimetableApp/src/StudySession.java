import java.time.LocalDate;

public class StudySession {
    private Chapter chapter;
    private LocalDate studyDate;
    private double hours;
    private boolean completed;

    public StudySession(Chapter chapter,LocalDate studyDate,double hours){
        this.chapter = chapter;
        this.studyDate = studyDate;
        this.hours = hours;
        this.completed = false;
    }

    public Chapter getChapter(){
        return chapter;
    }

    public LocalDate getDate(){
        return studyDate;
    }

    public double getHours(){
        return hours;
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
        System.out.println("Hours: "+hours);
        System.out.println("Completed: "+completed);

    }
}
