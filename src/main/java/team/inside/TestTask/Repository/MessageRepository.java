package team.inside.TestTask.Repository;

import org.springframework.data.repository.CrudRepository;
import team.inside.TestTask.Enteti.Message;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByid(Long id);
}
