package team.inside.MessageApplication.Repository;

import org.springframework.data.repository.CrudRepository;
import team.inside.MessageApplication.Enteties.Token;
public interface TokenRepository extends CrudRepository<Token, Long> {
    Token findBytoken(String token);
    Token findByid(Long id);
}
