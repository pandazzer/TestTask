package team.inside.TestTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.inside.TestTask.Components.MessagesService;
import team.inside.TestTask.Components.TokensService;
import team.inside.TestTask.Enteties.Token;
import team.inside.TestTask.Repository.MessagesRepository;
import team.inside.TestTask.Repository.TokenRepository;
import team.inside.TestTask.Repository.UserRepository;

@RestController
@RequestMapping
public class Controller {
    @Autowired
    private final MessagesRepository messageRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TokenRepository tokenRepository;

    private final ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

    public Controller(MessagesRepository messageRepository, UserRepository userRepository, TokenRepository tokenRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping(path = "/authentication")
    public ResponseEntity getToken(@RequestBody String json) throws JsonProcessingException {
        return context.getBean(TokensService.class).getResponseForUser(json);
    }

    @PostMapping(path = "/message")
    public ResponseEntity sendMessage(@RequestHeader("token") String tokenWithBearer, @RequestBody String json) throws JsonProcessingException {
        String tokenWithoutBarer = tokenWithBearer.substring(7);    // удаление слова "Bearer" из полученого токена
        boolean tokenIsValid = context.getBean(TokensService.class).validToken(tokenWithoutBarer);
        Token token = context.getBean(TokensService.class).findToken(tokenWithoutBarer);
        return context.getBean(MessagesService.class).getResponseForMessage(json, tokenIsValid, token);
    }
}
