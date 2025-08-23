package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validator.ChronologicalDates;
import ru.practicum.shareit.validator.OnCreate;

import java.time.LocalDateTime;

@Data
@ChronologicalDates
public class BookingRequestDto {
    @NotNull(message = "id вещи не может быть null", groups = {OnCreate.class})
    private Long itemId;

    @NotNull(message = "Дата начала бронирования обязательна", groups = {OnCreate.class})
    @Future(message = "Дата начала бронирования должна быть в будущем", groups = {OnCreate.class})
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования обязательна", groups = {OnCreate.class})
    @Future(message = "Дата окончания бронирования должна быть в будущем", groups = {OnCreate.class})
    private LocalDateTime end;
}