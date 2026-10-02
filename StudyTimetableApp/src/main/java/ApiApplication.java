import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.ArrayList;

public class ApiApplication{

    public static void main(String[] args){
        ArrayList<Module> modules = StudyStorage.load();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper,true));
        
            config.routes.get("/modules", ctx ->{
                ctx.json(modules);
            });
        }
        );

        app.start(7000);
    }
}
