import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class StudySlot {
    
    private LocalDate date;
    private LocalTime sTime;
    private LocalTime eTime;

    public StudySlot(LocalDate date,LocalTime sTime,LocalTime eTime){
        this.date = date;
        this.sTime = sTime;
        this.eTime = eTime;
    }

    public LocalDate getSlotDate(){
        return date;
    }

    public LocalTime getETime(){
        return eTime;
    }

    public LocalTime getSTime(){
        return sTime;
    }

    public double getDurationHours(){
        Duration duration = Duration.between(sTime,eTime);
        double hours = duration.toMinutes() / 60.0;
        return hours;
    }

    public LocalTime getEndTimeForDuration(double hours){
        long minutes = (long)(hours*60);
        return sTime.plusMinutes(minutes);
    }

    public void displaySlot(){
        System.out.println("Study Slot:");
        System.out.println("-------------------");
        System.out.println("Date: "+date);
        System.out.println("Time: "+sTime+" - "+eTime);
    } 
}
