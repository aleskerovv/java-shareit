package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoInform;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {ItemRepository.class, UserMapper.class})
public interface BookingMapper {
    BookingDtoResponse toBookingDtoResponse(Booking booking);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoInform toBookingDtoInform(Booking booking);


    @Mapping(target = "item", source = "itemId", qualifiedBy = BaseMapper.class)
    @Mapping(target = "startDate", source = "start")
    @Mapping(target = "endDate", source = "end")
    Booking toBookingEntity(BookingDtoCreate bookingDtoCreate);

    @Mapping(target = "start", source = "startDate")
    @Mapping(target = "end", source = "endDate")
    @Mapping(target = "itemId", source = "item.id")
    BookingDtoCreate toBookingDtoCreate(Booking booking);
}
