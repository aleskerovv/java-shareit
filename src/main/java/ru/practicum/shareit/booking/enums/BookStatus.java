package ru.practicum.shareit.booking.enums;

public enum BookStatus {
    WAITING("WAITING"),
    APPROVED("true"),
    REJECTED("false"),
    CANCELED("CANCELED");

    private final String label;

    BookStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static BookStatus fromLabel(String label) {
        for (BookStatus currentEnum : BookStatus.values()) {
            if (currentEnum.getLabel().equals(label))
                return currentEnum;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
