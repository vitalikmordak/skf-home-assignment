package com.skf.homeassignment.service;

import com.skf.homeassignment.domain.Message;
import com.skf.homeassignment.exception.MessageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageRepository messageRepository;

	public void publish(Message message) {
		messageRepository.create(message);
	}

	public Message getLast() {
		return messageRepository.getLast().orElseThrow(()-> new MessageNotFoundException("No messages found."));
	}

	public List<Message> getByTime(Instant start, Instant end) {
		return messageRepository.getByTime(start, end);
	}
}
