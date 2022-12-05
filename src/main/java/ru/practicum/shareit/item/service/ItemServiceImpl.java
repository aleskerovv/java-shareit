package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoInform;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<ItemDtoResponse> getItemsByOwnerId(Long userId) {
        List<Item> items = itemRepository.getItemsByOwnerId(userId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());

        return this.setBookingsDates(items);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ItemDtoResponse getById(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);

        return this.setBookingsDates(item, userId);
    }

    @Override
    public ItemDtoResponse create(ItemDto itemDto, Long userId) {
        Item item = itemMapper.toItemEntity(itemDto, userId);
        item.setOwner(userMapper.toUserEntity(userService.getUserById(userId)));
        return itemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemDtoResponse update(ItemDto itemDto, Long itemId, Long userId) {
        this.checkItemsOwner(itemId, userId);

        Item item = itemRepository.getReferenceById(itemId);

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription()
                : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable()
                : item.getAvailable());

        return itemMapper.toItemDtoResponse(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<ItemDtoResponse> findByParams(String params) {
        if (params.isEmpty() || params.isBlank()) return new ArrayList<>();

        return itemRepository.getItemsByParams(params.toLowerCase())
                .stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }

    private void checkItemsOwner(Long itemId, Long userId) {
        if (!Objects.equals(itemRepository.getReferenceById(itemId).getOwner().getId(), userId)) {
            throw new NoAccessException("You have no access to edit this item");
        }
    }

    private ItemDtoResponse setBookingsDates(Item item, Long userId) {
        ItemDtoResponse itemDto = itemMapper.toItemDtoResponse(item);

        if (Objects.equals(itemDto.getOwner().getId(), userId)) {
            List<BookingDtoInform> bookings = bookingRepository.findBookingsByItemId(itemDto.getId(),
                            LocalDateTime.now()).stream()
                    .limit(2)
                    .sorted(Comparator.comparing(Booking::getStartDate))
                    .map(bookingMapper::toBookingDtoInform)
                    .collect(Collectors.toList());

            itemDto.setLastBooking(!bookings.isEmpty() ? bookings.get(0) : null);
            itemDto.setNextBooking(bookings.size() == 2 ? bookings.get(1) : null);
        }

        return itemDto;
    }

    private List<ItemDtoResponse> setBookingsDates(List<Item> items) {
       List<ItemDtoResponse> itemsDto = items.stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());

        itemsDto.forEach(itemDto -> {
            List<BookingDtoInform> bookings = bookingRepository.findBookingsByItemId(itemDto.getId(),
                            LocalDateTime.now()).stream()
                    .limit(2)
                    .sorted(Comparator.comparing(Booking::getStartDate))
                    .map(bookingMapper::toBookingDtoInform)
                    .collect(Collectors.toList());
            itemDto.setLastBooking(!bookings.isEmpty() ? bookings.get(0) : null);
            itemDto.setNextBooking(bookings.size() == 2 ? bookings.get(1) : null);
        });

        return itemsDto;
    }
}
