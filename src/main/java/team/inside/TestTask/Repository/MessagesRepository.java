package team.inside.TestTask.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import team.inside.TestTask.Enteties.Message;

import java.util.List;
@Repository
public interface MessagesRepository extends CrudRepository<Message, Long> {

    List<Message> findByid(Long id);
}
