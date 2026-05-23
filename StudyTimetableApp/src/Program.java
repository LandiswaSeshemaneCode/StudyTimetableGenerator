import java.util.ArrayList;
import java.util.Scanner;

public class Program {

    public static void main(String[] args){
        new Program();
    }

    ArrayList<Module> module = new ArrayList<>();
    ArrayList<Assessment> assessment = new ArrayList<>();

    public Program(){

    }

    public void addModule(ArrayList<Module> m){
        Scanner modName = new Scanner(System.in);
        System.out.print("Enter the module name: ");
        String mName = modName.nextLine();
        System.out.println("\n");

        Module module = new Module(mName.toUpperCase());
        Scanner numberOfChapters = new Scanner(System.in);
        System.out.print("How many chapters does "+mName+" have? : ");
        int numChapters = numberOfChapters.nextInt();

        for(int i= 0;i<numChapters;i++){
            module.addChapter(addChapter());
            System.out.println("Chapter added successfully.");
        }

        m.add(module);
    }

    public Chapter addChapter(){
        Scanner chapterName = new Scanner(System.in);
        System.out.print("Enter the Chapter name: ");
        String cName = chapterName.nextLine();


        Scanner chapterDifficulty = new Scanner(System.in);
        System.out.print("Enter the Chapter difficulty 1-(easy) to 5-(hard): ");
        int cDifficulty = chapterDifficulty.nextInt();
        System.out.println("\n");

        Scanner chapterConfidence = new Scanner(System.in);
        System.out.print("Enter your confidence in the chapter from 0-100: ");
        double cConfidence = chapterConfidence.nextDouble();
        //this is basically the mark a user thinks they would get in
        // a test covering this chapter

        return new Chapter(cName,cDifficulty,cConfidence);
    }

}
