package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedItemAccessException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(Long userId, BookingRequestDto dto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();

        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Нельзя бронировать свою вещь.");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }

        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedItemAccessException("Только владелец вещи может изменять статус бронирования");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже было подтверждено или отклонено.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking updated = bookingRepository.save(booking);
        return bookingMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new UnauthorizedItemAccessException("Пользователь не имеет доступа к этому бронированию");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(Long userId, String stateParam) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        BookingState state = BookingState.from(stateParam);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForOwner(Long ownerId, String stateParam) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        BookingState state = BookingState.from(stateParam);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT ->
                    bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }
}