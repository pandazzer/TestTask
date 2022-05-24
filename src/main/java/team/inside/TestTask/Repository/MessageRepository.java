package team.inside.TestTask.Repository;

import org.springframework.data.repository.CrudRepository;
import team.inside.TestTask.Enteti.Message;

public interface MessageRepository extends CrudRepository<Message, Long> {
    Message findByid(Long id);
}
