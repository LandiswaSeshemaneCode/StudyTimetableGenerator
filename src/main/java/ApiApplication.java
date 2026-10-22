import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.ArrayList;

public class ApiApplication{

    public static void main(String[] args){
        ArrayList<Module> modules = StudyStorage.load();
        StudyPlanService service = new StudyPlanService();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper,true));
        
            //GET: Retrieve modules
            config.routes.get("/modules", ctx ->{
                ctx.json(modules);
            });

            //POST: Create new module
            config.routes.post("/modules",ctx ->{

                CreateModuleRequest request = ctx.bodyAsClass(CreateModuleRequest.class);

                String moduleName = request.moduleName();

                if(moduleName == null || moduleName.isBlank()){
                    ctx.status(400).json(
                        java.util.Map.of(
                            "error","Module name cannot be empty"
                        )
                    );
                    return;
                }
                
                //Using the existing bussiness logic 
                
                Module newModule = service.createModule(moduleName.trim());

                modules.add(newModule); //adding newly created module

                StudyStorage.save(modules); //saving to storage

                ctx.status(201).json(newModule); //returns the module
            });

            //GET: Retrieve Chapter
            config.routes.get("/modules/{id}/chapters",ctx->{

                int moduleID;

                try{
                    moduleID = Integer.parseInt(ctx.pathParam("id"));
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                }

                if(moduleID < 0 || moduleID >= modules.size()){
                    ctx.status(404).json(
                        java.util.Map.of("error","Module not found")
                    );
                    return;
                }

                Module module = modules.get(moduleID);

                ctx.json(module.getChapters()); //used the arraylist here not the linkedlist of chapters
            });

            //POST:Create chapter

            config.routes.post("/modules/{id}/chapters",ctx ->{
                int moduleID;

                try{
                    moduleID = Integer.parseInt(ctx.pathParam("id"));
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                }

                if(moduleID < 0 || moduleID >= modules.size()){
                    ctx.status(404).json(
                        java.util.Map.of("error","Module not found")
                    );
                    return;
                }

                CreateChapterRequest request = ctx.bodyAsClass(CreateChapterRequest.class);

                if(request.chapterName() == null || request.chapterName().isBlank()
                || request.chapterDifficulty() > 5 || request.chapterDifficulty() < 1
                || !Double.isFinite(request.chapterConfidence())
                || request.chapterConfidence()<0 || request.chapterConfidence() > 100){

                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid Chapter Information")
                    );

                    return;
                }

                Module module = modules.get(moduleID);

                Chapter chapter = service.createChapter(request.chapterName().trim(),request.chapterDifficulty(),
                 request.chapterConfidence());

                 service.addChapter(module,chapter);

                 StudyStorage.save(modules);

                 ctx.status(201).json(chapter);

            });
        });

        app.start(7000);
    }
}
