package team.inside.TestTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableJpaRepositories("team.inside.TestTask.Repository")
public class TestTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestTaskApplication.class, args);
	}

}
