package team.inside.TestTask.Repository;

import org.springframework.data.repository.CrudRepository;
import team.inside.TestTask.Enteties.User;
public interface UserRepository extends CrudRepository<User,Long> {
    User findByusers(String user);
}
