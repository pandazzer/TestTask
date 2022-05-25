package team.inside.TestTask.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import team.inside.TestTask.Constant;
import team.inside.TestTask.Enteties.Message;
import team.inside.TestTask.Enteties.Token;
import team.inside.TestTask.Json.JsonMessage;
import team.inside.TestTask.Json.JsonSomeMessage;
import team.inside.TestTask.Repository.MessagesRepository;
import team.inside.TestTask.Repository.TokenRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Component(value = "Message")
public class MessagesService implements Constant {

    private final TokenRepository tokenRepository;
    private final MessagesRepository messageRepository;
    Logger log = LogManager.getLogger();

    public MessagesService(TokenRepository tokenRepository, MessagesRepository messageRepository) {
        this.tokenRepository = tokenRepository;
        this.messageRepository = messageRepository;
    }

    public ResponseEntity getResponseForMessage (String tokenWithoutBarer, String json, boolean isValid) throws JsonProcessingException {

        if (!isValid){                                                       // проверка токена на соответсвие нынешнему ключу (ключ генерируется при каждом новом запуске программы)
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
            String[] arrayMessage = getArrayMessages(list, numberMessage);   // получение нужного количества последних сообщений от пользователя

            JsonSomeMessage jsonSomeMessage = new JsonSomeMessage();        // перевод сообщений в json и отправка пользователю
            jsonSomeMessage.setMessage(arrayMessage);
            String jsonResponse = mapper.writeValueAsString(jsonSomeMessage);

            return new ResponseEntity(jsonResponse, HttpStatus.OK);
        }

        Message messageToDB = new Message(token.getId(), message, new Date());  // запись полученного сообщения в бд
        messageRepository.save(messageToDB);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    public String[] getArrayMessages(List<Message> list, int numberMessage){
        ArrayList<String> result = new ArrayList<>(numberMessage);

        if (list.size()>=numberMessage){
            List<Message> newList;
            newList = list.subList(list.size() - numberMessage, list.size());
            for (Message message : newList){
                result.add(message.getMessage());
            }
        }else {
            for (Message message : list){
                result.add(message.getMessage());
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
