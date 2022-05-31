package team.inside.TestTask.Components;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import team.inside.TestTask.Enteti.Message;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Component(value = "Token")
public class Service {

    Logger log = LogManager.getLogger();

    private final Key key;

    public Service() {
        this.key = MacProvider.generateKey();
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

    public String[] getArrayMessage(List<Message> list, int numberMessage){
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
