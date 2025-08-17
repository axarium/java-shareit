package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.OnCreate;
import ru.practicum.shareit.validator.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(
            @PathVariable @Positive Long id,
            @Validated(OnUpdate.class) @RequestBody UserDto userDto
    ) {
        return userService.update(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable @Positive Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @Positive Long id) {
        userService.deleteById(id);
    }
}