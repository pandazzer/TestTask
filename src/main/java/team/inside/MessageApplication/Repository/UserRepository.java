package team.inside.MessageApplication.Repository;

import org.springframework.data.repository.CrudRepository;
import team.inside.MessageApplication.Enteties.User;
public interface UserRepository extends CrudRepository<User,Long> {
    User findByusers(String user);
}
