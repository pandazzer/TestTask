package team.inside.TestTask.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import team.inside.TestTask.Enteties.User;
@Repository
public interface UserRepository extends CrudRepository<User,Long> {
    User findByusers(String user);
}
