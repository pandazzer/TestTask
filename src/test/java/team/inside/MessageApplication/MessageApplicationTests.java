package team.inside.MessageApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.inside.MessageApplication.Json.JsonMessage;
import team.inside.MessageApplication.Json.JsonToken;
import team.inside.MessageApplication.Json.JsonUser;
import team.inside.MessageApplication.Repository.MessagesRepository;
import team.inside.MessageApplication.Repository.TokenRepository;
import team.inside.MessageApplication.Repository.UserRepository;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MessageApplicationTests {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private MessagesRepository messagesRepository;
	private static String url = System.getenv("TEST_URL");

	@Test()
	void contextLoads() {
		assertThat(userRepository).isNotNull();
		assertThat(tokenRepository).isNotNull();
		assertThat(messagesRepository).isNotNull();
	}
	@BeforeAll
	static void sfd() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonUser jsonUser = new JsonUser();
		jsonUser.setName("Tester");
		jsonUser.setPassword("password123");
		String user = mapper.writeValueAsString(jsonUser);
		given().baseUri(url)
				.body(user)
				.when().post("/authentication")
				.then()
				.statusCode(200)
				.contentType(ContentType.JSON);
	}
	@Test
	void UserAuthorizationTest() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonUser jsonUser = new JsonUser();
		jsonUser.setName("Tester");
		jsonUser.setPassword("password123");
		String user = mapper.writeValueAsString(jsonUser);
		Response response = given().baseUri(url)
				.body(user)
				.when().post("/authentication")
				.then()
				.statusCode(200)
				.contentType(ContentType.JSON)
				.extract().response();
		String actualToken = mapper.readValue(response.getBody().asString(), JsonToken.class ).getToken();
		String expectedToken = tokenRepository.findByid(userRepository.findByusers("Tester").getId()).getToken();
		assertThat(actualToken).isEqualTo(expectedToken);
	}

	@Test
	void UserUnAuthorizationTest() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonUser jsonUser = new JsonUser();
		jsonUser.setName("Tester1");
		jsonUser.setPassword("password123");
		String user = mapper.writeValueAsString(jsonUser);
		given().baseUri(url)
				.body(user)
				.when().post("/authentication")
				.then()
				.statusCode(404);
	}

	@Test
	void UserUnAuthorizationPasswordTest() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonUser jsonUser = new JsonUser();
		jsonUser.setName("Tester");
		jsonUser.setPassword("password1231");
		String user = mapper.writeValueAsString(jsonUser);
		given().baseUri(url)
				.body(user)
				.when().post("/authentication")
				.then()
				.statusCode(401);
	}

	@Test
	void messageSaveTest() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonMessage jsonMessage = new JsonMessage();
		jsonMessage.setName("Tester");
		jsonMessage.setMessage("Some message");
		String message = mapper.writeValueAsString(jsonMessage);
		String token = tokenRepository.findByid(userRepository.findByusers("Tester").getId()).getToken();
		Header header = new Header("token", "Bearer_" + token);
		given().baseUri(url)
				.header(header)
				.body(message)
				.when().post("/message")
				.then()
				.statusCode(201);
	}
	@Test
	void getMessageHistoryTest() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonMessage jsonMessage = new JsonMessage();
		jsonMessage.setName("Tester");
		jsonMessage.setMessage("history 10");
		String message = mapper.writeValueAsString(jsonMessage);
		String token = tokenRepository.findByid(userRepository.findByusers("Tester").getId()).getToken();
		Header header = new Header("token", "Bearer_" + token);
		Response response = given().baseUri(url)
				.header(header)
				.body(message)
				.when().post("/message")
				.then()
				.statusCode(200)
				.contentType(ContentType.JSON)
				.extract().response();
		List<String> list = response.jsonPath().getList("message");
		assertThat(list.size()).isEqualTo(10);
	}
}
