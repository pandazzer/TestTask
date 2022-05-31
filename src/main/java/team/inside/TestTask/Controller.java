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
import team.inside.TestTask.Components.TokensService;
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
        JsonUser jsonUser = mapper.readValue(json, JsonUser.class); // перевод из полученного json в объект jsonUser

        User user = userRepository.findByusers(jsonUser.getName()); // поиск user-а по имени в базе данных
        if (user == null){
            log.info("Пользователь не найден");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        if (!jsonUser.getPassword().equals(user.getPassword())){    // сравнение полученного пароля с паролем записанным в базе
            log.info("Имя или пароль не подходит");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        String newToken = context.getBean(TokensService.class).getToken(jsonUser.getName());     // генерация токена
        Token token = new Token(user.getId(), newToken, new Date());    // запись токена в бд или перезапись токена если у пользователя уже он был
        tokenRepository.save(token);

        JsonToken jsonToken = new JsonToken();                      // создание и отправка пользователю токена в формате json
        jsonToken.setToken(newToken);
        String jsonResponse = mapper.writeValueAsString(jsonToken);

        return new ResponseEntity(jsonResponse,HttpStatus.OK);
    }

    @PostMapping(path = "/message")
    public ResponseEntity sendMessage(@RequestHeader("token") String tokenWithBearer, @RequestBody String json) throws JsonProcessingException {
        String tokenWithoutBarer = tokenWithBearer.substring(7);    // удаление слова "Bearer" из полученого токена
        if (!context.getBean(TokensService.class).validToken(tokenWithoutBarer)){                          // проверка токена на соответсвие нынешнему ключу (ключ генерируется при каждом новом запуске программы)
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        Token token = tokenRepository.findBytoken(tokenWithoutBarer);         // поиск токена в базе и проверка на наличие и срок действия
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

        ObjectMapper mapper = new ObjectMapper();                               // перевод из полученного json в объект jsonMessage
        JsonMessage jsonMessage = mapper.readValue(json, JsonMessage.class);
        String message = jsonMessage.getMessage();

        String[] messageArray = message.split(" ");                     // разбиение сообщения и проверка на соответсвие формата "history N"
        if (messageArray[0].equals("history")){
            int numberMessage = Integer.parseInt(messageArray[1]);

            List<Message> list = messageRepository.findByid(token.getId());     // поиск всех сообщений в бд по индексу пользователя, полученный из токена
            String[] arrayMessage = context.getBean(TokensService.class).getArrayMessage(list, numberMessage);   // получение нужного количества последних сообщений от пользователя

            JsonSomeMessage jsonSomeMessage = new JsonSomeMessage();        // перевод сообщений в json и отправка пользователю
            jsonSomeMessage.setMessage(arrayMessage);
            String jsonResponse = mapper.writeValueAsString(jsonSomeMessage);

            return new ResponseEntity(jsonResponse, HttpStatus.OK);
        }

        Message messageToDB = new Message(token.getId(), message, new Date());  // запись полученного сообщения в бд
        messageRepository.save(messageToDB);

        return new ResponseEntity(HttpStatus.CREATED);
    }
}
