package ru.practicum.shareit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

public class ChronologicalDatesValidator implements ConstraintValidator<ChronologicalDates, BookingRequestDto> {

    @Override
    public boolean isValid(BookingRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            return false;
        }
        return dto.getStart().isBefore(dto.getEnd());
    }
}