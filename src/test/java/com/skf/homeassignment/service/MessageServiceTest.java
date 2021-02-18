package com.skf.homeassignment.service;

import com.skf.homeassignment.domain.Message;
import com.skf.homeassignment.exception.MessageNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private MessageRepository messageRepository;

	@InjectMocks
	private MessageService messageService;

	@Test
	void publish() {
		Message message = new Message("Some content");

		messageService.publish(message);

		verify(messageRepository).create(message);
	}

	@Test
	void getLast() {
		Message expectedMessage = new Message("foo bar");

		when(messageRepository.getLast()).thenReturn(Optional.of(expectedMessage));

		Message last = messageService.getLast();

		assertEquals(expectedMessage, last);
	}

	@Test
	void getLast_not_exists() {
		when(messageRepository.getLast()).thenReturn(Optional.empty());

		MessageNotFoundException exception = assertThrows(MessageNotFoundException.class, () -> messageService.getLast());

		assertEquals("No messages found.", exception.getMessage());
	}

	@Test
	void getByTime() {
		List<Message> expectedMessages = Arrays.asList(new Message("content 1"), new Message("content 2"));
		Instant end = Instant.now();
		Instant start = end.minus(2, ChronoUnit.DAYS);

		when(messageRepository.getByTime(start, end)).thenReturn(expectedMessages);

		List<Message> actualMessages = messageService.getByTime(start, end);

		assertThat(actualMessages).containsAll(expectedMessages);
	}

	@Test
	void getByTime_not_exists() {
		Instant end = Instant.now();
		Instant start = end.minus(2, ChronoUnit.DAYS);

		when(messageRepository.getByTime(start, end)).thenReturn(Collections.emptyList());

		List<Message> actualMessages = messageService.getByTime(start, end);

		assertTrue(actualMessages.isEmpty());
	}
}
