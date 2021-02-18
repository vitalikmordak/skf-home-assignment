package com.skf.homeassignment.infrastructure.repository;

import com.skf.homeassignment.domain.Message;
import com.skf.homeassignment.infrastructure.repository.dto.MappedMessage;
import com.skf.homeassignment.service.MessageRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RedisMessageRepository implements MessageRepository {
	private static final String KEY = "Message";

	private final HashOperations<String, UUID, MappedMessage> hashOperations;

	public RedisMessageRepository(final RedisTemplate<String, MappedMessage> redisTemplate) {
		hashOperations = redisTemplate.opsForHash();
	}

	@Override
	public void create(Message message) {
		MappedMessage mappedMessage = new MappedMessage();
		mappedMessage.setContent(message.getContent());

		hashOperations.put(KEY, mappedMessage.getId(),  mappedMessage);
	}

	@Override
	public Optional<Message> getLast() {
		return hashOperations.values(KEY).stream()
				.max(Comparator.comparing(MappedMessage::getCreatedAt))
				.map(msg-> new Message(msg.getContent()));
	}

	@Override
	public List<Message> getByTime(Instant start, Instant end) {
		return hashOperations.values(KEY).stream()
				.filter(msg -> msg.getCreatedAt().isAfter(start) && msg.getCreatedAt().isBefore(end))
				.map(msg -> new Message(msg.getContent()))
				.collect(Collectors.toList());
	}
}
