import java.time.LocalDate;

public record CreateAssessmentRequest(String assessmentName,LocalDate assessmentDate
    ,double goalConfidence){

}