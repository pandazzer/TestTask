package team.inside.TestTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.inside.TestTask.Component.Service;
import team.inside.TestTask.Enteti.Message;
import team.inside.TestTask.Enteti.Token;
import team.inside.TestTask.Enteti.User;
import team.inside.TestTask.Json.JsonMessage;
import team.inside.TestTask.Json.JsonSomeMessage;
import team.inside.TestTask.Json.JsonToken;
import team.inside.TestTask.Json.JsonUser;
import team.inside.TestTask.Repository.MessageRepository;
import team.inside.TestTask.Repository.TokenRepository;
import team.inside.TestTask.Repository.UserRepository;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping
public class Controller implements Constant {
    Logger log = LogManager.getLogger();

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final TokenRepository tokenRepository;
    private final Service context = new AnnotationConfigApplicationContext(Config.class).getBean(Service.class);

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
        String newToken = context.getToken(jsonUser.getName());
        Token token = new Token(user.getId(), newToken, new Date());
        tokenRepository.save(token);

        JsonToken jsonToken = new JsonToken();
        jsonToken.setToken(newToken);
        String jsonResponse = mapper.writeValueAsString(jsonToken);

        return new ResponseEntity(jsonResponse,HttpStatus.OK);
    }

    @PostMapping(path = "/message")
    public ResponseEntity sendMessage(@RequestHeader("token") String tokenWithBearer, @RequestBody String json) throws JsonProcessingException {
        String tokenWithoutBarer = tokenWithBearer.substring(7);
        context.validToken(tokenWithoutBarer);
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

        ObjectMapper mapper = new ObjectMapper();
        JsonMessage jsonMessage = mapper.readValue(json, JsonMessage.class);
        String message = jsonMessage.getMessage();

        String[] messageArray = message.split(" ");
        if (messageArray[0].equals("history")){

            int numberMessage = Integer.parseInt(messageArray[1]);
            List<Message> list = messageRepository.findByid(token.getId());
            String[] arrayMessage = context.getArrayMessage(list, numberMessage);

            JsonSomeMessage jsonSomeMessage = new JsonSomeMessage();
            jsonSomeMessage.setMessage(arrayMessage);
            String jsonResponse = mapper.writeValueAsString(jsonSomeMessage);

            return new ResponseEntity(jsonResponse, HttpStatus.OK);
        }

        Message messageToDB = new Message(token.getId(), message, new Date());
        messageRepository.save(messageToDB);

        return new ResponseEntity(HttpStatus.CREATED);
    }
}
