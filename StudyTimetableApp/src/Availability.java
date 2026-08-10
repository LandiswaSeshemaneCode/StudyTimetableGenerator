import java.time.*;
public class Availability {
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;

    public Availability(DayOfWeek day,LocalTime startTime,LocalTime endTime){
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime(){
        return  startTime;
    }

    public LocalTime getEndTime(){
        return endTime;
    }


    public DayOfWeek getDayOfWeek(){
        return day;
    }

    public double getAvailableHours(){
        long minutes = Duration.between(startTime, endTime).toMinutes();
        return minutes/60.0;
    }

    public void displayAvailability(){
        System.out.println(day);
        System.out.println(startTime+ "-"+endTime);
        System.out.println("Hours available: "+getAvailableHours());
    }
}
