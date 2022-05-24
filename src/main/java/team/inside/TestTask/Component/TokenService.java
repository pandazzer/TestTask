package team.inside.TestTask.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component(value = "Token")
public class TokenService {

    private final Key key;

    public TokenService() {
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


}
