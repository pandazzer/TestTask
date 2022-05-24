package team.inside.TestTask;

import org.springframework.data.repository.CrudRepository;
import team.inside.TestTask.Enteti.User;

public interface UserRepository extends CrudRepository<User,Long> {
    User findByusers(String user);
}
