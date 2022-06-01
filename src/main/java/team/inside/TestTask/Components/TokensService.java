package team.inside.TestTask.Components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import team.inside.TestTask.Constant;
import team.inside.TestTask.Enteties.Token;
import team.inside.TestTask.Enteties.User;
import team.inside.TestTask.Json.JsonToken;
import team.inside.TestTask.Json.JsonUser;
import team.inside.TestTask.Repository.TokenRepository;
import team.inside.TestTask.Repository.UserRepository;

import java.security.Key;
import java.util.Date;

@Component(value = "Token")
public class TokensService implements Constant {

    Logger log = LogManager.getLogger();

    private final Key key;
    @Autowired
    public TokensService() {
        this.key = MacProvider.generateKey();
    }

    public ResponseEntity getResponseForUser(String json, UserRepository userRepository, TokenRepository tokenRepository) throws JsonProcessingException {
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
        String newToken = getToken(jsonUser.getName());     // генерация токена
        Token token = new Token(user.getId(), newToken, new Date());    // запись токена в бд или перезапись токена если у пользователя уже он был
        tokenRepository.save(token);

        JsonToken jsonToken = new JsonToken();                      // создание и отправка пользователю токена в формате json
        jsonToken.setToken(newToken);
        String jsonResponse = mapper.writeValueAsString(jsonToken);

        return new ResponseEntity(jsonResponse,HttpStatus.OK);
    }

    public String getToken(String userName){
        String compactJws = Jwts.builder()
                .setSubject(userName)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return compactJws;
    }

    public boolean validToken(String token){
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            log.info("Токен валиден");
            return true;
        }catch (SignatureException e){
            log.info("Неправильный токен");
            return false;
        }
    }

    public Token findToken(String tokenWithoutBarer, TokenRepository tokenRepository) {
        Token token = tokenRepository.findBytoken(tokenWithoutBarer);         // поиск токена в базе
        return token;
    }
}
