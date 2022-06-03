package team.inside.MessageApplication.Repository;

import org.springframework.data.repository.CrudRepository;
import team.inside.MessageApplication.Enteties.Message;

import java.util.List;
public interface MessagesRepository extends CrudRepository<Message, Long> {

    List<Message> findByid(Long id);
}
