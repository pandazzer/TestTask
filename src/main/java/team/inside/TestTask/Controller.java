package team.inside.TestTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.inside.TestTask.Enteti.JsonUser;

@RestController
@RequestMapping
public class Controller {
    Logger log = LogManager.getLogger();

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    @Autowired
    public Controller(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping(path = "/authentication")
    public String getToken(@RequestBody String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonUser jsonUser = mapper.readValue(json, JsonUser.class);
        System.out.println(jsonUser.getName() + " " + jsonUser.getPassword());
        return null;
    }
}
