package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingGatewayDto {
	@NotNull
	private Long itemId;
	@NotNull
	@FutureOrPresent(message = "'start' must be future or present date")
	private LocalDateTime start;
	@NotNull
	@Future(message = "'end' must be in future")
	private LocalDateTime end;
}
