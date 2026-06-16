import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.Comparator;

public class Program {

    public static void main(String[] args){
        new Program();
    }

    ArrayList<Module> module = new ArrayList<>();
    ArrayList<Assessment> assessment = new ArrayList<>();

    Scanner input = new Scanner(System.in);

    public Program(){
        addModule(module);
        System.out.println("Modules saved: "+module.size());
        System.out.println("Assessments saved: "+assessment.size());
        displayAssessments();
        displayGlobalStudyRecommendations();
        System.out.println("Thank you");
    }

    public void addModule(ArrayList<Module> m){
        
        System.out.print("Enter the module name: ");
        String mName = input.nextLine().toUpperCase();
        System.out.println("");

        Module mod = new Module(mName); // Self-explanatory and for easier use later
        System.out.print("How many chapters does "+mName+" have? : ");
        int numChapters = Integer.parseInt(input.nextLine());

        for(int i= 0;i<numChapters;i++){
            mod.addChapter(AddChapter());
            System.out.println("Chapter added successfully.");
        }

        System.out.print("How many assessments does "+ mName+ " have: ");
        int numAssessments = Integer.parseInt(input.nextLine());

        for(int i = 0;i<numAssessments;i++){
            assessment.add(createAssessment(mod));
        }
        System.out.println("");

        m.add(mod);
    }

    public Chapter AddChapter(){
        
        System.out.print("Enter the Chapter name: ");
        String cName = input.nextLine();
        System.out.println("");

        System.out.print("Enter the Chapter difficulty 1-(easy) to 5-(hard): ");
        int cDifficulty = Integer.parseInt(input.nextLine());
        System.out.println("");

        System.out.print("Enter your confidence in the chapter from 0-100: ");
        double cConfidence = Double.parseDouble(input.nextLine());
        System.out.println("");
        //this is basically the mark a user thinks they would get in
        // a test covering this chapter

        return new Chapter(cName,cDifficulty,cConfidence);
    }

    public Assessment createAssessment(Module mod){
    
        System.out.print("Enter the assessment name: ");
        String aName = input.nextLine();
        System.out.println("");

        System.out.print("Enter the assessment date (yyyy-mm-dd): ");
        LocalDate aDate = LocalDate.parse(input.nextLine());
        System.out.println("");

        System.out.print("Enter you goal confidence (0-100): ");
        double aGoal = Double.parseDouble(input.nextLine());
        System.out.println("");

        Assessment newAssessment =  new Assessment(aName, aDate, aGoal, mod);
        selectAssessedChapter(mod, newAssessment);
        System.out.println("");
        return newAssessment;
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
                assessment.addAssessedChapter(selectChapter);
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
        for(int i = 0;i<assessment.size();i++){
            Assessment displayA = assessment.get(i);
            displayA.displayAssessment();
        }
    }

    public void displayGlobalStudyRecommendations(){
        ArrayList<StudyRecommendation> studyRecs = new ArrayList<>();
        for(int i=0;i<assessment.size();i++){
            Assessment assessed = assessment.get(i);
            ArrayList<Chapter> assessedChapters = assessed.getAssessedChapters();
            for(int j = 0;j<assessedChapters.size();j++){
                Chapter chapter = assessedChapters.get(j);
                StudyRecommendation studyRecommendation = new StudyRecommendation(chapter,assessed);
                studyRecs.add(studyRecommendation);
            }   
        }

        studyRecs.sort(Comparator.comparingDouble(StudyRecommendation::getFinalScore).reversed());
        System.out.println("\nGlobal Study Recommendation:");
        System.out.println("-------------------------------");
        for(int k = 0;k<studyRecs.size();k++){
            studyRecs.get(k).displayRecommendation(k+1);
        }
    }
}
