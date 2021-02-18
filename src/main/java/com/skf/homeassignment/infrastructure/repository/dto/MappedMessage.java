package com.skf.homeassignment.infrastructure.repository.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MappedMessage implements Serializable{

	private UUID id = UUID.randomUUID();

	private String content;

	private Instant createdAt = Instant.now();
}
