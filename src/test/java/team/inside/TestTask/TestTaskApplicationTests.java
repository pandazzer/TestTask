package team.inside.TestTask;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import team.inside.TestTask.Components.MessagesService;
import team.inside.TestTask.Repository.UserRepository;

import java.util.LinkedList;
@SpringBootTest
class TestTaskApplicationTests {
	@Autowired
	private UserRepository userRepository;

	@Test()
	void contextLoads() {
	}

}
