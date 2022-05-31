package team.inside.TestTask.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import team.inside.TestTask.Enteties.Token;
@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {
    Token findBytoken(String token);
}
