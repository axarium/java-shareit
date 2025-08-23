package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.validator.OnCreate;

import java.util.List;

import static ru.practicum.shareit.constant.Constants.USER_HEADER;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader(USER_HEADER) @Positive Long userId,
            @RequestBody @Validated(OnCreate.class) BookingRequestDto bookingDto
    ) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(
            @RequestHeader(USER_HEADER) @Positive Long ownerId,
            @PathVariable @Positive Long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.updateBookingStatus(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader(USER_HEADER) @Positive Long userId,
            @PathVariable @Positive Long bookingId
    ) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(
            @RequestHeader(USER_HEADER) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwner(
            @RequestHeader(USER_HEADER) @Positive Long ownerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingsForOwner(ownerId, state);
    }
}