package com.skf.homeassignment.entrypoint;

import com.skf.homeassignment.domain.Message;
import com.skf.homeassignment.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("v1/messages")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;

	@PostMapping("publish")
	@ResponseStatus(HttpStatus.CREATED)
	public void create(@RequestBody Message message) {
		messageService.publish(message);
	}

	@GetMapping("getByTime")
	public List<Message> getByTimeInterval(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
										   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end) {
		return messageService.getByTime(start.toInstant(), end.toInstant());
	}

	@GetMapping("getLast")
	public Message getLast() {
		return messageService.getLast();
	}
}
