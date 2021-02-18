package com.skf.homeassignment.service;

import com.skf.homeassignment.domain.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MessageRepository {

	void create(Message message);

	Optional<Message> getLast();

	List<Message> getByTime(Instant start, Instant end);
}
