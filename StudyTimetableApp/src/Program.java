import java.util.ArrayList;
import java.util.Scanner;

public class Program {

    public static void main(String[] args){
        new Program();
    }

    ArrayList<Module> module = new ArrayList<>();
    ArrayList<Assessment> assessment = new ArrayList<>();

    Scanner input = new Scanner(System.in);

    public Program(){
        addModule(module);
        System.out.println("Thank you");
    }

    public void addModule(ArrayList<Module> m){
        
        System.out.print("Enter the module name: ");
        String mName = input.nextLine();
        System.out.println("");

        Module mod = new Module(mName.toUpperCase());
        System.out.print("How many chapters does "+mName+" have? : ");
        int numChapters = input.nextInt();

        for(int i= 0;i<numChapters;i++){
            mod.addChapter(AddChapter());
            System.out.println("Chapter added successfully.");
        }

        m.add(mod);
    }

    

    public Chapter AddChapter(){
        
        System.out.print("Enter the Chapter name: ");
        String cName = input.nextLine();
        System.out.println("");

        System.out.print("Enter the Chapter difficulty 1-(easy) to 5-(hard): ");
        int cDifficulty = input.nextInt();
        System.out.println("");

        System.out.print("Enter your confidence in the chapter from 0-100: ");
        double cConfidence = input.nextDouble();
        System.out.println("");
        //this is basically the mark a user thinks they would get in
        // a test covering this chapter

        return new Chapter(cName,cDifficulty,cConfidence);
    }

}
