package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(
            @RequestHeader(USER_HEADER) @Positive Long userId,
            @Validated(OnCreate.class) @RequestBody ItemDto itemDto
    ) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(USER_HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @Validated(OnUpdate.class) @RequestBody ItemDto itemDto
    ) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable @Positive Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(USER_HEADER) @Positive Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchAvailableItems(text);
    }
}