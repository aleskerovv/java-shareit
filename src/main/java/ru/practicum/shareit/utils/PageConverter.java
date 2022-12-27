package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageConverter {
    public static PageRequest toPageRequest(int from, int size) {
        return PageRequest.of((from / size), size);
    }
}
