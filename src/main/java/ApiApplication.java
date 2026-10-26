import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import java.sql.*;
import java.util.Map;

import java.util.ArrayList;
public class ApiApplication{

    public static void main(String[] args){
        ModuleRepository repository = new ModuleRepository();
        ChapterRepository chapterRepository = new ChapterRepository();
        AssessmentRepository assessmentRepository = new AssessmentRepository();
        AssessedChapterRepository assessedChapterRepository = new AssessedChapterRepository();
        StudyPlanRepository studyPlanRepository = new StudyPlanRepository();
        StudySessionRepository studySessionRepository = new StudySessionRepository();

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

            //GET: Retrieve saved timetable
            config.routes.get("/timetable",ctx->{

                try{
                    ArrayList<StudySession> sessions = studySessionRepository.findByStudent(studentID);
                    ctx.json(sessions);
                }
                catch(java.sql.SQLException e){
                    e.printStackTrace();
                    ctx.status(500).json(
                        java.util.Map.of("error","Failed to retrieve timetable")
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

                   try{
                    ArrayList<Module> modules = studyPlanRepository.loadStudyPlan(studentID);

                    StudyRecommendationService recommendationService = new StudyRecommendationService(modules);
                    ArrayList<StudyRecommendation> recommendations = recommendationService.generateGlobalRecommendations();

                    TimetableGenerator generator = new TimetableGenerator();

                    
                    ArrayList<StudySession> savedSessions =
                            studySessionRepository.findByStudent(studentID);

                    ArrayList<StudySession> completedSessions = new ArrayList<>();

                    for (StudySession session : savedSessions) {
                        if (session.isCompleted()) {
                            completedSessions.add(session);
                        }
                    }

                    generator.generateTimetable(
                            recommendations,
                            availability,
                            request.daysAhead(),
                            completedSessions
                    );

                    studySessionRepository.saveTimetable(
                            studentID,
                            generator.getSessions()
                    );

                    ctx.json(generator.getSessions());

                   }catch(java.sql.SQLException e){
                    e.printStackTrace();
                    ctx.status(500).json(
                        java.util.Map.of("error","Failed to generate timetable")
                    );
                   }
           
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

                //PATCH: Mark sessions complete
                config.routes.patch("timetable/sessions/{id}/complete",ctx->{

                    long sessionID;

                    try{
                        sessionID = Long.parseLong(ctx.pathParam("id"));

                        if(sessionID<=0){
                            ctx.status(400).json(
                                java.util.Map.of("error","Invalid session ID.")
                            );
                            return;
                        }
                    }
                    catch(NumberFormatException e){
                        ctx.status(400).json(
                            java.util.Map.of("error","Invalid session ID")
                        );
                        return;
                    }

                    try{
                        boolean updated = studySessionRepository.markCompleted(sessionID, studentID);

                        if(!updated){
                           ctx.status(404).json(
                            java.util.Map.of("error","Incomplete session not found.")
                            );
                            return; 
                        }

                        ctx.json(
                            java.util.Map.of("message","Study session completed",
                                "sessionID",sessionID
                            )
                        );
                    }catch(SQLException e){
                        e.printStackTrace();

                        ctx.status(500).json(
                            java.util.Map.of("error","Failed to update study session.")
                        );
                        return;
                    }

                });
            });


        app.start(7000);
    }
}
