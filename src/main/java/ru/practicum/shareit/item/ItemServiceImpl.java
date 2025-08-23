package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedItemAccessException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithOwnerDto;
import ru.practicum.shareit.item.dto.ItemWithoutOwnerDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemWithOwnerDto createItem(Long ownerId, ItemRequestDto itemDto) {
        User owner = validateUserExists(ownerId);

        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);

        Item saved = itemRepository.save(item);
        return itemMapper.toItemDtoWithOwner(saved, List.of());
    }

    @Override
    public ItemWithOwnerDto updateItem(Long ownerId, Long itemId, ItemRequestDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedItemAccessException("Только владелец может обновлять вещь");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item updated = itemRepository.save(item);
        return itemMapper.toItemDtoWithOwner(updated, List.of());
    }

    @Override
    public ItemWithOwnerDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<Comment> comments = commentRepository.findByItemId(itemId);

        return itemMapper.toItemDtoWithOwner(item, comments);
    }

    @Override
    public List<ItemWithoutOwnerDto> getItemsByOwner(Long ownerId) {
        User owner = validateUserExists(ownerId);

        List<Item> items = itemRepository.findByOwner(owner);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        if (itemIds.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findByItemIdInAndStatus(itemIds, BookingStatus.APPROVED);
        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), List.of());

                    Booking lastBooking = itemBookings.stream()
                            .filter(b -> b.getEnd().isBefore(now))
                            .max(Comparator.comparing(Booking::getEnd))
                            .orElse(null);

                    Booking nextBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    List<Comment> itemComments = commentsByItemId.getOrDefault(item.getId(), List.of());

                    return itemMapper.toItemWithoutOwnerDto(item, lastBooking, nextBooking, itemComments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemWithOwnerDto> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchAvailableByText(text).stream()
                .map(item -> itemMapper.toItemDtoWithOwner(item, List.of()))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, String text) {
        User author = validateUserExists(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hasCompletedBooking = bookingRepository
                .findByBookerIdAndItemIdAndStatus(userId, itemId, BookingStatus.APPROVED).stream()
                .anyMatch(b -> b.getEnd().isBefore(LocalDateTime.now()));

        if (!hasCompletedBooking) {
            throw new ValidationException("Бронирование не закончилось");
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    private User validateUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}