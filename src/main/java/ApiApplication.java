import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.ArrayList;
public class ApiApplication{

    public static void main(String[] args){
        ArrayList<Module> modules = StudyStorage.load();
        ModuleRepository repository = new ModuleRepository();
        ChapterRepository chapterRepository = new ChapterRepository();
        AssessmentRepository assessmentRepository = new AssessmentRepository();
        AssessedChapterRepository assessedChapterRepository = new AssessedChapterRepository();
        //Temp development student
        //Authentication will replace this later
        final long studentID = 1;
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
                try{
                    ctx.json(repository.findByStudent(studentID));
                }catch(java.sql.SQLException e){
                    e.printStackTrace();
                    ctx.status(500).json(java.util.Map.of("error","Failed to retrieve modules"));
                }
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
                
                try{
                    long generatedID = repository.save(moduleName.trim(),studentID);

                    Module newModule = new Module(moduleName.trim());
                    newModule.setID(generatedID);
                
                ctx.status(201).json(newModule); //returns the module
                }
                catch(java.sql.SQLException e){
                    ctx.status(500).json(
                        java.util.Map.of("error","Failed to save module")
                    );
                }
            });

            //GET: Retrieve Chapter
            config.routes.get("/modules/{id}/chapters",ctx->{

                long moduleID;

                try{
                    moduleID = Long.parseLong(ctx.pathParam("id"));
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                }

                if(moduleID <= 0){
                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                   return;

                }

                try{
                    Module module = repository.findByID(moduleID, studentID);
                    
                    if(module == null){
                        ctx.status(404).json(
                            java.util.Map.of("error","Module not found")
                        );
                        return;

                    }
                   ctx.json(chapterRepository.findByModule(moduleID));

                }catch(java.sql.SQLException e){
                    e.printStackTrace();

                    ctx.status(500).json(
                        java.util.Map.of("error","Failed to retrieve chapters")
                    );
                }

            });

            //POST:Create chapter

            config.routes.post("/modules/{id}/chapters",ctx ->{
                long moduleID;

                try{
                    moduleID = Long.parseLong(ctx.pathParam("id"));
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                }

                if(moduleID <= 0){
                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                }

                CreateChapterRequest request = ctx.bodyAsClass(CreateChapterRequest.class);

                if(request.chapterName() == null || request.chapterName().isBlank()
                || request.chapterName().trim().length() > 150 || request.chapterDifficulty() < 1
                ||request.chapterDifficulty() > 5|| !Double.isFinite(request.chapterConfidence())
                || request.chapterConfidence()<0 || request.chapterConfidence() > 100){

                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid Chapter Information")
                    );

                    return;
                }

                try{
                    Module module = repository.findByID(moduleID, studentID);
                    
                    if(module == null){
                        ctx.status(404).json(
                            java.util.Map.of("error","Module not found")
                        );
                     return;

                    }

                    String chapterName = request.chapterName().trim();

                    long chapterID = chapterRepository.save(moduleID, chapterName, request.chapterDifficulty(),request.chapterConfidence());
                    
                    Chapter chapter = new Chapter(chapterName, request.chapterDifficulty(),request.chapterConfidence());
                    
                    chapter.setID(chapterID);

                    ctx.status(201).json(chapter);
                }catch(java.sql.SQLException e){
                    e.printStackTrace();

                    ctx.status(500).json(
                        java.util.Map.of("error","Failed to save chapter")
                    );
                }

            });

            //GET: Retrieve Assessment

            config.routes.get("/modules/{id}/assessments", ctx->{
                
                long moduleID;
                
                try{
                    moduleID = Long.parseLong(ctx.pathParam("id"));
                    if(moduleID <= 0 ){
                        ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                    }
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                }

                try{
                    Module module = repository.findByID(moduleID,studentID);
                    if(module ==null){
                    ctx.status(404).json(
                        java.util.Map.of("error","Module not found")
                    );
                    return;
                }

                ctx.json(assessmentRepository.findByModule(moduleID, module));
                }catch(java.sql.SQLException e){
                    e.printStackTrace();
                    ctx.status(500).json(
                        java.util.Map.of("error","Failed to retrieve assessments.")
                    );
                }

            });

            //POST: Create Assessment
            config.routes.post("/modules/{id}/assessments",ctx->{
                 long moduleID;
                
                try{
                    moduleID = Long.parseLong(ctx.pathParam("id"));
                    if(moduleID<=0){
                        ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                    }
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                        java.util.Map.of("error","Invalid module ID")
                    );
                    return;
                }

                CreateAssessmentRequest request = ctx.bodyAsClass(CreateAssessmentRequest.class);

                if(request.assessmentName() == null || request.assessmentName().isBlank()
                    || request.assessmentName().trim().length()>150||request.assessmentDate() == null 
                    || !Double.isFinite(request.goalConfidence())
                    || request.goalConfidence() < 0 || request.goalConfidence()>100){

                        ctx.status(400).json(
                            java.util.Map.of("error","Invalid Assessment Information")
                        );

                        return;
                    }

                    try{
                        Module module = repository.findByID(moduleID,studentID);
                        if(module==null){
                            ctx.status(404).json(
                                java.util.Map.of("error","Module not found.")
                            );
                            return;
                        }

                        String assessmentName = request.assessmentName().trim();

                        long assessmentID = assessmentRepository.save(moduleID,assessmentName,
                            request.assessmentDate(),request.goalConfidence());

                        Assessment assessment = new Assessment(assessmentName,request.assessmentDate(),
                        request.goalConfidence(), module);

                        assessment.setID(assessmentID);
                        ctx.status(201).json(assessment);
                    }catch(java.sql.SQLException e){
                        e.printStackTrace();
                        ctx.status(500).json(
                            java.util.Map.of("error","Failed to save assessment")
                        );
                    }
            });
            
            //GET: Retreive assessed chapters
            config.routes.get("/modules/{id}/assessments/{assessmentID}/chapters",ctx->{
                long moduleID,assessmentID;

                try{
                    moduleID = Long.parseLong(ctx.pathParam("id"));
                    assessmentID = Long.parseLong(ctx.pathParam("assessmentID"));

                    if(moduleID <= 0 || assessmentID<=0){
                         ctx.status(400).json(
                    java.util.Map.of("error","Invalid ID")
                    );
                    return;
                    }
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                    java.util.Map.of("error","Invalid ID")
                    );
                    return;
                }


                try{
                    Module module = repository.findByID(moduleID, studentID);

                    if(module == null){
                        ctx.status(404).json(
                            java.util.Map.of("error","Module not found")
                        );
                        return;
                    }

                    Assessment assessment = assessmentRepository.findByID(assessmentID, moduleID, module);

                    if(assessment == null){
                        ctx.status(404).json(
                            java.util.Map.of("error","Assessment not found")
                        );
                        return;
                    }

                    ctx.json(assessedChapterRepository.findByAssessment(assessmentID));
                }catch(java.sql.SQLException e){
                    e.printStackTrace();
                    ctx.status(500).json(
                        java.util.Map.of("error","Failed to retrieve assessed chapters")
                    );
                }
               
            });

            //POST: Add assessed chapters 
            config.routes.post("/modules/{id}/assessments/{assessmentID}/chapters",ctx->{
                 long moduleID,assessmentID;

                try{
                    moduleID = Long.parseLong(ctx.pathParam("id"));
                    assessmentID = Long.parseLong(ctx.pathParam("assessmentID"));

                    if(moduleID <= 0 || assessmentID<=0){
                         ctx.status(400).json(
                    java.util.Map.of("error","Invalid ID")
                    );
                    return;
                    }
                }catch(NumberFormatException e){
                    ctx.status(400).json(
                    java.util.Map.of("error","Invalid ID")
                    );
                    return;
                }

                AddAssessedChapter request = ctx.bodyAsClass(AddAssessedChapter.class);

                long chapterID = request.chapterID();
                if(chapterID<=0){
                      ctx.status(400).json(
                        java.util.Map.of("error","Invalid chapter ID.")
                    );
                    return;
                }

              try{
                Module module = repository.findByID(moduleID,studentID);

                if(module==null){
                    ctx.status(404).json(
                        java.util.Map.of("error","Module not found")
                    );
                    return;
                }

                Assessment assessment = assessmentRepository.findByID(assessmentID,moduleID,module);

                if(assessment == null){
                    ctx.status(404).json(
                        java.util.Map.of("error","Assessment not found")
                    );
                    return;
                }

                boolean saved = assessedChapterRepository.save(assessmentID,chapterID);

                if(!saved){
                     ctx.status(404).json(
                        java.util.Map.of("error","Chapter not found in this module.")
                    );
                    return;
                }

                ctx.status(201).json(
                    java.util.Map.of(
                        "message","Chapter linked successfully",
                        "assessmentID",assessmentID,
                        "chapterID",chapterID
                    )
                );
              }catch(java.sql.SQLIntegrityConstraintViolationException e){
                e.printStackTrace();
                ctx.status(409).json(
                    java.util.Map.of(
                        "error","Chapter already linked to assessment."
                    )
                );
              }catch(java.sql.SQLException e){
                 ctx.status(500).json(
                        java.util.Map.of("error","Failed to link chapter")
                    );
              }

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
