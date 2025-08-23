package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithOwnerDto;
import ru.practicum.shareit.item.dto.ItemWithoutOwnerDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface ItemMapper {

    ItemShortDto toItemShortDto(Item item);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "ownerId", source = "item.owner.id")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    ItemWithOwnerDto toItemDtoWithOwner(Item item, List<Comment> comments);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "lastBooking", source = "lastBooking", qualifiedByName = "toBookingShortDto")
    @Mapping(target = "nextBooking", source = "nextBooking", qualifiedByName = "toBookingShortDto")
    @Mapping(target = "comments", source = "comments")
    ItemWithoutOwnerDto toItemWithoutOwnerDto(
            Item item,
            Booking lastBooking,
            Booking nextBooking,
            List<Comment> comments
    );

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "available", expression = "java(dto.getAvailable() != null && dto.getAvailable())")
    Item toItem(ItemRequestDto dto);

    @Named("toBookingShortDto")
    default BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }
}