import java.io.*;
import java.util.ArrayList;
public class StudyStorage {

    private static final String FILE_NAME = "studyplan.dat";

    public static void save(ArrayList<Module> modules){
        try{
            FileOutputStream file = new FileOutputStream(FILE_NAME);
            ObjectOutputStream output = new ObjectOutputStream(file);

            output.writeObject(modules);
            output.close();
            file.close();
        }
        catch(IOException e){
            System.out.println("Error saving study plan.");
            e.printStackTrace(); //reports the error that caused the crash
        }
    }

    public static ArrayList<Module> load(){

        try{
            FileInputStream file = new FileInputStream(FILE_NAME);
            ObjectInputStream input = new ObjectInputStream(file);

            ArrayList<Module> modules = (ArrayList<Module>)input.readObject();

            input.close();
            file.close();
            System.out.println("Study plan loaded successfully!");
            return modules;
        }
        catch(IOException |ClassNotFoundException e ){
            System.out.println("No saved study plan.");
            return new ArrayList<>();
        }
    }
    
}
