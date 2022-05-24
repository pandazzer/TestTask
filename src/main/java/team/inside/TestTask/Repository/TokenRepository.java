package team.inside.TestTask.Repository;

import org.springframework.data.repository.CrudRepository;
import team.inside.TestTask.Enteti.Token;

public interface TokenRepository extends CrudRepository<Token, Long> {
    Token findBytoken(String token);
}
