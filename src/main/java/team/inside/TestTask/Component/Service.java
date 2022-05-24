package team.inside.TestTask.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.stereotype.Component;
import team.inside.TestTask.Enteti.Message;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Component(value = "Token")
public class Service {

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
            Jwts.parser().setSigningKey(key).parseClaimsJwt(token);
            return true;
        }catch (SignatureException e){
            return false;
        }
    }

    public String[] getArrayMessage(List<Message> list, int numberMessage){
        ArrayList<String> result = new ArrayList<>(numberMessage);

        if (list.size()>=numberMessage){
            List<Message> newList = new ArrayList<>();
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
