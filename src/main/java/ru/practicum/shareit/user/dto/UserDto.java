package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.validator.OnCreate;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым", groups = {OnCreate.class})
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой", groups = {OnCreate.class})
    @Email(message = "Электронная почта не соответствует формату", groups = {OnCreate.class})
    private String email;
}