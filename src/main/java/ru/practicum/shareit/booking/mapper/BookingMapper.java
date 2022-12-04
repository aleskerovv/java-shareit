package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {ItemRepository.class, UserMapper.class})
public interface BookingMapper {

    BookingDtoResponse toBookingDtoResponse(Booking booking);

    @Mapping(target = "item", source = "bookingDtoCreate.itemId", qualifiedBy = BaseMapper.class)
    @Mapping(target = "startDate", source = "bookingDtoCreate.start")
    @Mapping(target = "endDate", source = "bookingDtoCreate.end")
    Booking toBookingEntity(BookingDtoCreate bookingDtoCreate);
}
