package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.validator.OnCreate;

@Data
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым", groups = {OnCreate.class})
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = {OnCreate.class})
    private String description;

    @NotNull(message = "Статус не может быть пустым", groups = {OnCreate.class})
    private Boolean available;

    private Long ownerId;
}