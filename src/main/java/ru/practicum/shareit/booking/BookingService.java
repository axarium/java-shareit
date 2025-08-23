package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingRequestDto bookingDto);

    BookingDto updateBookingStatus(Long bookingId, Long ownerId, boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByUser(Long userId, String state);

    List<BookingDto> getBookingsForOwner(Long ownerId, String state);
}