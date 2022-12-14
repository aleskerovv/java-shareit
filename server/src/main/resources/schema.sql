CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_name   VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    owner_id    BIGINT                                  NOT NULL,
    request_id  BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT ITEMS_USERS_FK
        foreign key (owner_id) references users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP                               NOT NULL,
    end_date   TIMESTAMP                               NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    booker_id  BIGINT                                  NOT NULL,
    status     VARCHAR(100)                            NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT BOOKINGS_ITEMS_FK
        foreign key (item_id) references items (id) ON DELETE CASCADE,
    CONSTRAINT BOOKINGS_USERS_FK
        foreign key (booker_id) references users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(1000)                           NOT NULL,
    item_id   BIGINT                                  NOT NULL,
    author_id BIGINT                                  NOT NULL,
    created   TIMESTAMP                               NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT COMMENTS_ITEMS_FK
        foreign key (item_id) references items (id) ON DELETE CASCADE,
    CONSTRAINT COMMENTS_USERS_FK
        foreign key (author_id) references users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(1000)                           NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    created      TIMESTAMP                               NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT REQUESTS_USERS_FK
        foreign key (requester_id) references users (id) ON DELETE CASCADE
);