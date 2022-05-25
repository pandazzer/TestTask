package team.inside.TestTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.inside.TestTask.Component.MessagesService;
import team.inside.TestTask.Component.TokensService;

@RestController
@RequestMapping
public class Controller {

    private final ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

    @PostMapping(path = "/authentication")
    public ResponseEntity getToken(@RequestBody String json) throws JsonProcessingException {
        return context.getBean(TokensService.class).getResponseForUser(json);
    }

    @PostMapping(path = "/message")
    public ResponseEntity sendMessage(@RequestHeader("token") String tokenWithBearer, @RequestBody String json) throws JsonProcessingException {
        String tokenWithoutBarer = tokenWithBearer.substring(7);    // удаление слова "Bearer" из полученого токена
        boolean tokenIsValid = context.getBean(TokensService.class).validToken(tokenWithoutBarer);
        return context.getBean(MessagesService.class).getResponseForMessage(tokenWithoutBarer, json, tokenIsValid);
    }
}
