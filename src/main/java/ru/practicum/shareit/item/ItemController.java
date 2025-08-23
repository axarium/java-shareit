package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import java.util.List;

import static ru.practicum.shareit.constant.Constants.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemWithOwnerDto createItem(
            @RequestHeader(USER_HEADER) @Positive Long userId,
            @Validated(OnCreate.class) @RequestBody ItemRequestDto itemDto
    ) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemWithOwnerDto updateItem(
            @RequestHeader(USER_HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @Validated(OnUpdate.class) @RequestBody ItemRequestDto itemDto
    ) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithOwnerDto getItemById(@PathVariable @Positive Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemWithoutOwnerDto> getItemsByOwner(@RequestHeader(USER_HEADER) @Positive Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemWithOwnerDto> searchItems(@RequestParam String text) {
        return itemService.searchAvailableItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(USER_HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @Validated @RequestBody CommentRequestDto commentRequestDto
    ) {
        return itemService.addComment(userId, itemId, commentRequestDto.getText());
    }
}