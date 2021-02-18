package com.skf.homeassignment.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skf.homeassignment.config.TestRedisConfiguration;
import com.skf.homeassignment.domain.Message;
import com.skf.homeassignment.infrastructure.repository.dto.MappedMessage;
import com.skf.homeassignment.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestRedisConfiguration.class)
class MessageControllerIntegrationTest {

	private static final String KEY = "Message";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MessageService messageService;

	@Autowired
	private RedisTemplate<String, MappedMessage> redisTemplate;

	private HashOperations<String, UUID, MappedMessage> hashOperations;

	@BeforeEach
	void init() {
		hashOperations = redisTemplate.opsForHash();
	}

	@AfterEach
	void cleanAfterTest() {
		Object[] messageUuids = hashOperations.values(KEY).stream().map(MappedMessage::getId).toArray(UUID[]::new);

		if (messageUuids.length > 0) {
			hashOperations.delete(KEY, messageUuids);
		}
	}

	@Test
	void publish() throws Exception {
		Message message = new Message("test content");
		ObjectMapper mapper = new ObjectMapper();
		String request = mapper.writeValueAsString(message);

		mockMvc.perform(post("/v1/messages/publish")
				.contentType(MediaType.APPLICATION_JSON)
				.content(request))
				.andDo(print())
				.andExpect(status().isCreated());

		Message last = messageService.getLast();
		assertEquals(message, last);
	}

	@Test
	void getLast() throws Exception {
		MappedMessage mappedMessage = new MappedMessage();
		mappedMessage.setContent("bar baz");
		hashOperations.put(KEY, mappedMessage.getId(), mappedMessage);

		mockMvc.perform(get("/v1/messages/getLast"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").value(mappedMessage.getContent()));
	}

	@Test
	void getLast_not_exists() throws Exception {
		mockMvc.perform(get("/v1/messages/getLast"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void getByTime() throws Exception {
		MappedMessage mappedMessage = new MappedMessage();
		mappedMessage.setContent("foo");
		hashOperations.put(KEY, mappedMessage.getId(), mappedMessage);
		MappedMessage mappedMessage2 = new MappedMessage();
		mappedMessage2.setContent("var");
		hashOperations.put(KEY, mappedMessage2.getId(), mappedMessage2);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date end = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
		Date start = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));

		mockMvc.perform(get("/v1/messages/getByTime")
				.queryParam("start", dateFormat.format(start))
				.queryParam("end", dateFormat.format(end)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$.[*].content", containsInAnyOrder(mappedMessage.getContent(), mappedMessage2.getContent())));
	}
}
