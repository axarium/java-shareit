package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent(existing -> {
            throw new EmailAlreadyExistsException("Пользователь с данной электронной почтой уже существует");
        });

        User user = userMapper.toUser(userDto);
        User saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userRepository.findByEmail(userDto.getEmail()).ifPresent(other -> {
                if (!other.getId().equals(id)) {
                    throw new EmailAlreadyExistsException("Данная электронная почта занята другим пользователем");
                }
            });
            user.setEmail(userDto.getEmail());
        }

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }
}