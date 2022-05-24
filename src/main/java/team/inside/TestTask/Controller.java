package team.inside.TestTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.inside.TestTask.Component.TokenService;
import team.inside.TestTask.Enteti.Token;
import team.inside.TestTask.Json.JsonToken;
import team.inside.TestTask.Json.JsonUser;
import team.inside.TestTask.Enteti.User;
import team.inside.TestTask.Repository.MessageRepository;
import team.inside.TestTask.Repository.TokenRepository;
import team.inside.TestTask.Repository.UserRepository;

import java.util.Date;

@RestController
@RequestMapping
public class Controller implements Constant {
    Logger log = LogManager.getLogger();

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final TokenRepository tokenRepository;
    private final ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

    @Autowired
    public Controller(UserRepository userRepository, MessageRepository messageRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping(path = "/authentication")
    public ResponseEntity getToken(@RequestBody String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonUser jsonUser = mapper.readValue(json, JsonUser.class);

        User user = userRepository.findByusers(jsonUser.getName());
        if (user == null){
            log.info("Пользователь не найден");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (!jsonUser.getPassword().equals(user.getPassword())){
            log.info("Имя или пароль не подходит");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        String newToken = context.getBean(TokenService.class).getToken(jsonUser.getName());
        Token token = new Token(user.getId(), newToken, new Date());
        tokenRepository.save(token);

//        String a = Jwts.parser().setSigningKey(key).parseClaimsJws(newToken).getHeader().toString();
//        String ab = Jwts.parser().setSigningKey(key).parseClaimsJws(newToken).getBody().toString();

        JsonToken jsonToken = new JsonToken();
        jsonToken.setToken(newToken);
        String jsonResponse = mapper.writeValueAsString(jsonToken);

        return new ResponseEntity(jsonResponse,HttpStatus.OK);
    }
    @PostMapping(path = "/message")
    public ResponseEntity sendMessage(@RequestHeader("token") String tokenWithBearer, @RequestBody String json){
        String tokenWithoutBarer = tokenWithBearer.substring(7);
        context.getBean(TokenService.class).validToken(tokenWithoutBarer);
        Token token = tokenRepository.findBytoken(tokenWithoutBarer);
        if (token == null){
            log.info("токен не найден");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        long timeNow = new Date().getTime();
        long timeWrite = token.getDate().getTime();
        if (timeNow - timeWrite >= validTime){
            log.info("токен устарел");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok("sd");
    }
}
