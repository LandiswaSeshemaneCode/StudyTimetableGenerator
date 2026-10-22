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
        mapper.disable(
    com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
); //formats time 
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper,true));

            config.bundledPlugins.enableCors(cors->{
                cors.addRule(rule->{
                    rule.allowHost("http://localhost:5173"); //5173 dev port for Vite. (frontend)
                });
            });
        
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

            //GET: Retrieve Assessment

            config.routes.get("/modules/{id}/assessments", ctx->{
                
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
                ctx.json(module.getAssessments());

            });

            //POST: Create Assessment
            config.routes.post("/modules/{id}/assessments",ctx->{
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

                CreateAssessmentRequest request = ctx.bodyAsClass(CreateAssessmentRequest.class);

                if(request.assessmentName() == null || request.assessmentName().isBlank()
                    || request.assessmentDate() == null || !Double.isFinite(request.goalConfidence())
                    || request.goalConfidence() < 0 || request.goalConfidence()>100){

                        ctx.status(400).json(
                            java.util.Map.of("error","Invalid Assessment Information")
                        );

                        return;
                    }

                    Module module = modules.get(moduleID);
                    Assessment assessment = service.createAssessment(request.assessmentName().trim(),
                     request.assessmentDate(), request.goalConfidence(), module);

                     module.addAssessment(assessment);
                     StudyStorage.save(modules);

                     ctx.status(201).json(assessment);
            });
            
            //GET: Retreive assessed chapters
            config.routes.get("/modules/{id}/assessments/{assessmentID}/chapters",ctx->{
                int moduleID,assessmentID;

                try{
                    moduleID = Integer.parseInt(ctx.pathParam("id"));
                    assessmentID = Integer.parseInt(ctx.pathParam("assessmentID"));
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                    java.util.Map.of("error","Invalid ID")
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

                if(assessmentID<0 || assessmentID >= module.getAssessments().size()){
                    ctx.status(404).json(
                        java.util.Map.of("error","Assessment not found")
                    );
                    return;
                }

                Assessment assessment = module.getAssessments().get(assessmentID);

                ctx.json(assessment.getAssessedChapters());
            });

            //POST: Add assessed chapters 
            config.routes.post("/modules/{id}/assessments/{assessmentID}/chapters",ctx->{
                 int moduleID,assessmentID;

                try{
                    moduleID = Integer.parseInt(ctx.pathParam("id"));
                    assessmentID = Integer.parseInt(ctx.pathParam("assessmentID"));
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                    java.util.Map.of("error","Invalid ID")
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

                if(assessmentID<0 || assessmentID >= module.getAssessments().size()){
                    ctx.status(404).json(
                        java.util.Map.of("error","Assessment not found")
                    );
                    return;
                }

                AddAssessedChapter request = ctx.bodyAsClass(AddAssessedChapter.class);
                Chapter chapter = module.getChapterAt(request.chapterID());

                if(chapter == null){
                    ctx.status(404).json(
                        java.util.Map.of("error","Chapter not found")
                    );
                    return;
                }

                Assessment assessment = module.getAssessments().get(assessmentID);

                if(assessment.getAssessedChapters().contains(chapter)){
                    ctx.status(409).json(
                        java.util.Map.of("error","Chapter already assigned")
                    );
                    return;
                }

                service.addAssessedChapter(chapter, assessment);
                StudyStorage.save(modules);
                ctx.status(200).json(assessment);
            });

            //POST:Generate timetable
            config.routes.post("/timetable/generate", ctx->{

                GenerateTimetableRequest request = ctx.bodyAsClass(GenerateTimetableRequest.class);

                if(request.daysAhead() < 1 || request.daysAhead() > 365
                    || request.availability() == null || request.availability().isEmpty()){
                        ctx.status(400).json(
                            java.util.Map.of("error","Invalid timetable information")
                        );
                        return;
                    }

                    ArrayList<Availability> availability = new ArrayList<>();
                    
                    for(AvailabilityRequest slot : request.availability()){
                        if(slot == null || slot.day() == null
                            || slot.startTime() == null || slot.endTime()==null
                            ||!slot.startTime().isBefore(slot.endTime())){

                                ctx.status(400).json(
                                    java.util.Map.of("error","Invalid availability slot")
                                );
                                return;
                        }
                      availability.add(new Availability(slot.day(),slot.startTime(),slot.endTime()));
                    }

                    StudyRecommendationService recommendationService = new StudyRecommendationService(modules);
                    ArrayList<StudyRecommendation> recommendations = recommendationService.generateGlobalRecommendations();

                    TimetableGenerator generator = new TimetableGenerator();

                    generator.generateTimetable(recommendations, availability, request.daysAhead());
                    ctx.json(generator.getSessions());

           
                });

                config.routes.exception(
                    io.javalin.http.BadRequestResponse.class,
                    (exception,ctx)->{
                        ctx.status(400).json(
                            java.util.Map.of("error","Invalid request data")
                        );

                    });

                    config.routes.exception(
                    com.fasterxml.jackson.core.JsonProcessingException.class,
                    (exception, ctx) -> {
                     ctx.status(400).json(
                        java.util.Map.of("error", "Invalid JSON request")
                        
                    );
                    });
            });


        app.start(7000);
    }
}
