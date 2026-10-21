import java.util.ArrayList;
import java.util.Scanner;
import java.time.*;


public class Program {

    public static void main(String[] args){
        new Program();
    }

    ArrayList<Module> module = new ArrayList<>();
    ArrayList<StudyRecommendation> studyRecs = new ArrayList<>();
    StudyPlanService studyPlanService = new StudyPlanService();

    Scanner input = new Scanner(System.in);

    public Program(){
        run();
        
    }

    public void run(){
         System.out.print("Load previous study plan (y/n)? : ");
        String choice = input.nextLine();
        
        if(choice.equalsIgnoreCase("Y")){
            module = StudyStorage.load();
        }
        else{
            System.out.print("How many modules do you have? : ");
        int numModules = Integer.parseInt(input.nextLine());
        for(int i = 0;i<numModules;i++){
        addModule();
        }
    }
        System.out.println("Modules saved: "+module.size());
        int totalAssessments = 0;
        for(int k =0;k<module.size();k++){
            totalAssessments+=module.get(k).getAssessments().size();
        }
        System.out.println("Assessments saved: "+totalAssessments);
        System.out.println("\n");
        displayAssessments();
        generateGlobalStudyRecommendations();
        System.out.println("\nGlobal Study Recommendations:");
        System.out.println("-------------------------------");
        for(int k = 0;k<studyRecs.size();k++){
            studyRecs.get(k).displayRecommendation(k+1);
                        System.out.println("\n");
        }
        System.out.println("\n");

        ArrayList<Availability> studyAvailability = scheduleAvailability();
        TimetableGenerator timetable = new TimetableGenerator();
        System.out.print("How many days ahead do you want a timetable for: ");
        timetable.generateTimetable(studyRecs, studyAvailability,Integer.parseInt(input.nextLine()));
        timetable.displayTimetable();
        System.out.println("\n");
        System.out.println("Failed Recommendations: ");
        System.out.println("----------------------------------");
        for(StudyRecommendation rec : timetable.getFailedRecommendations()){
            System.out.println(rec.getChapter().getChapterName());
            System.out.println("Required Study hours: "+rec.getRequiredStudyHours());
            System.out.println("Hours studied: "+rec.completedStudyHours());
            System.out.println("Earliest assessment: "+rec.earliestDate());
            System.out.println("\n");

        }
        StudyStorage.save(module);
        System.out.println("Thank you");
    }

public ArrayList<Availability> scheduleAvailability(){
    ArrayList<Availability> studyAvailability = new ArrayList<>();
    System.out.print("How many days of this week will you be available: ");
    int days = Integer.parseInt(input.nextLine());
    for(int i = 0;i<days;i++){
        System.out.println("1.Monday");
        System.out.println("2.Tuesday");
        System.out.println("3.Wednesday");
        System.out.println("4.Thursday");
        System.out.println("5.Friday");
        System.out.println("6.Saturday");
        System.out.println("7.Sunday");
        System.out.print("What day will you be available: ");
        int choice = Integer.parseInt(input.nextLine());
       try{ 
            DayOfWeek chosenDay = DayOfWeek.of(choice);
            System.out.print("How many study sessions can have on "+chosenDay+" : ");
            int slots = Integer.parseInt(input.nextLine());
            for(int j = 1;j<slots+1;j++){
            System.out.print("What time will you be available to start on "+chosenDay+" for slot "+j+" (HH:mm): ");
            LocalTime startAt = LocalTime.parse(input.nextLine());
            System.out.print("What time will your availability end (HH:mm): ");
            LocalTime endAt = LocalTime.parse(input.nextLine());
            if(!endAt.isAfter(startAt)){
                System.out.print("Enter a time after "+startAt+" (HH:mm):");
                endAt = LocalTime.parse(input.nextLine());
            }
            studyAvailability.add(new Availability(chosenDay,startAt,endAt));
            }
        }
            catch(Exception e){
                System.out.println(e.getLocalizedMessage());
                int choose = 0;
                while(choose < 1 || choose >7){
                System.out.println("Enter a correct number 1-7: ");
                System.out.println("1.Monday");
                System.out.println("2.Tuesday");
                System.out.println("3.Wednesday");
                System.out.println("4.Thursday");
                System.out.println("5.Friday");
                System.out.println("6.Saturday");
                System.out.println("7.Sunday");
                System.out.print("What day will you be available: ");
                choose = Integer.parseInt(input.nextLine());
                }
                DayOfWeek chosenDay = DayOfWeek.of(choose);
            System.out.print("How many study sessions can you allocate on "+chosenDay+" : ");
            int slots = Integer.parseInt(input.nextLine());
            for(int j = 1;j<slots+1;j++){
            System.out.print("What time will you be available to start on "+chosenDay+" for slot "+j+" (HH:mm): ");
            LocalTime startAt = LocalTime.parse(input.nextLine());
            System.out.print("What time will your availability end (HH:mm): ");
            LocalTime endAt = LocalTime.parse(input.nextLine());
            if(!endAt.isAfter(startAt)){
                System.out.print("Enter a time after "+startAt+" (HH:mm):");
                endAt = LocalTime.parse(input.nextLine());
            }
            studyAvailability.add(new Availability(chosenDay,startAt,endAt));
            }

                }
                

            } 
        
        return studyAvailability;
    }


   public void addModule(){
    System.out.print("Enter the module name: ");
    String mName = input.nextLine().toUpperCase();

    Module mod = studyPlanService.createModule(mName);

    System.out.print("How many chapters does " + mName + " have? : ");
    int numChapters = Integer.parseInt(input.nextLine());

    for(int i = 0; i < numChapters; i++){
        addChapter(mod);
    }

    System.out.print("How many assessments does " + mName + " have? : ");
    int numAssessments = Integer.parseInt(input.nextLine());

    for(int i = 0; i < numAssessments; i++){
        createAssessment(mod);
    }

    module.add(mod);
}

     public void addChapter(Module mod){
        System.out.print("Enter the Chapter name: ");
        String cName = input.nextLine();
        System.out.print("Enter the Chapter difficulty 1-(easy) to 5-(hard): ");
        int cDifficulty = Integer.parseInt(input.nextLine());
        System.out.print("Enter your confidence in the chapter from 0-100: ");
        double cConfidence = Double.parseDouble(input.nextLine());
       Chapter chapter =  studyPlanService.createChapter(cName, cDifficulty, cConfidence);
        studyPlanService.addChapter(mod,chapter);
    }

   public void createAssessment(Module mod){
    System.out.print("Enter the assessment name: ");
    String aName = input.nextLine();

    System.out.print("Enter the assessment date (yyyy-mm-dd): ");
    LocalDate aDate = LocalDate.parse(input.nextLine());

    System.out.print("Enter you goal confidence (0-100): ");
    double aGoal = Double.parseDouble(input.nextLine());

    Assessment assessment = studyPlanService.createAssessment(
            aName, aDate, aGoal, mod
    );

    mod.addAssessment(assessment);
   selectAssessedChapter(mod, assessment);
}

    public void selectAssessedChapter(Module mod,Assessment assessment){
        System.out.println("Select the chapters that will be assessed: ");
        mod.displayChapters();

        System.out.print("Enter chapter number or 0 to stop: ");
        int choice = Integer.parseInt(input.nextLine());
        System.out.println("");

        while(choice!=0){
            Chapter selectChapter = mod.getChapterAt(choice);

            if(selectChapter!= null){
                studyPlanService.addAssessedChapter(selectChapter,assessment);
                System.out.println("Chapter added successfully.");
            }
            else{
                System.out.println("Invalid chapter number: ");
            }

            System.out.print("Enter chapter number or 0 to stop: ");
            choice = Integer.parseInt(input.nextLine());
        }
    } 


    public void displayAssessments(){
        for(int i = 0;i<module.size();i++){
            Module currentModule = module.get(i);
            ArrayList<Assessment> displayA = currentModule.getAssessments();
            for(int j = 0;j<displayA.size();j++){
                displayA.get(j).displayAssessment();
                System.out.println();
            }
        }
    }

    public void generateGlobalStudyRecommendations(){
        StudyRecommendationService recommendationService = new StudyRecommendationService(module);
        studyRecs = recommendationService.generateGlobalRecommendations();
    }

    
}
