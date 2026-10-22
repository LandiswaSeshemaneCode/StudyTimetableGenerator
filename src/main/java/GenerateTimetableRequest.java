import java.util.ArrayList;
public record GenerateTimetableRequest(int daysAhead,ArrayList<AvailabilityRequest> availability){

}