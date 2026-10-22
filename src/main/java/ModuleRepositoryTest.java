
import java.util.ArrayList;

public class ModuleRepositoryTest {

    public static void main(String[] args) {
        try {
            ModuleRepository repository = new ModuleRepository();

            // Replace 1 with your development student's actual ID.
            long studentId = 1;

            long newId = repository.save(
                "Computer Science", studentId
            );

            System.out.println("Saved module with ID: " + newId);

            ArrayList<Module> modules =
                repository.findByStudent(studentId);

            System.out.println("Modules in database:");

            for (Module module : modules) {
                System.out.println(module.getModuleName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
