package ru.practicum.shareit.booking;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public enum BookingState {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    FUTURE("FUTURE"),
    WAITING("WAITING"),
    REJECTED("REJECTED");

    private final String label;
    private static final Set<String> STATE_PARAMS = new HashSet<>();

    BookingState(String label) {
        this.label = label;
    }

    static {
        Stream.of(BookingState.values())
                .forEach(v -> STATE_PARAMS.add(v.label));
    }

    public static BookingState valueOfLabel(String value) {
        return Stream.of(BookingState.values())
                .filter(v -> v.label.equals(value))
                .findFirst().orElse(null);
    }

    public static Set<String> getStateParams() {
        return STATE_PARAMS;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
