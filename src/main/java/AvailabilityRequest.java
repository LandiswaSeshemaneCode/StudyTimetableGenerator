import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityRequest(DayOfWeek day,LocalTime startTime, LocalTime endTime){

}