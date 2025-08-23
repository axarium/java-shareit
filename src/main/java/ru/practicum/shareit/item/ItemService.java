package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemWithOwnerDto;
import ru.practicum.shareit.item.dto.ItemWithoutOwnerDto;

import java.util.List;

public interface ItemService {

    ItemWithOwnerDto createItem(Long ownerId, ItemRequestDto itemDto);

    ItemWithOwnerDto updateItem(Long ownerId, Long itemId, ItemRequestDto itemDto);

    ItemWithOwnerDto getItemById(Long itemId);

    List<ItemWithoutOwnerDto> getItemsByOwner(Long ownerId);

    List<ItemWithOwnerDto> searchAvailableItems(String text);

    CommentDto addComment(Long userId, Long itemId, String text);
}