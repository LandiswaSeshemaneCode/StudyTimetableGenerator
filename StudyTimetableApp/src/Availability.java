public class Availability {
    private String day;
    private double availableHours;

    public Availability(String day,double availableHours){
        this.day = day;
        this.availableHours = availableHours;
    }

    public String getDay(){
        return day;
    }

    public double getAvailableHours(){
        return availableHours;
    }

    public void displayAvailability(){
        System.out.println("Day: "+day);
        System.out.println("Hours: "+availableHours);
    }
}
