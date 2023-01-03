package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.utils.PageConverter;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts = {"file:src/test/resources/test-schema.sql", "file:src/test/resources/test-data.sql"})
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    private static final Pageable PAGEABLE = PageConverter.toPageRequest(0, 10);

    @Test
    void getItemsByOwnerId_andReturns_2() {
        List<Item> items = itemRepository.getItemsByOwnerId(1L, PAGEABLE);

        assertThat(items.size(), equalTo(2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ITEM", "item"})
    void getItemsByParams_andReturns_3(String params) {
        List<Item> items = itemRepository.getItemsByParams(params, PAGEABLE);

        assertThat(items.size(), equalTo(3));
    }

    @Test
    void getItemsByItemRequestId_andReturns_1() {
        List<Item> items = itemRepository.getItemsByItemRequestId(1L);

        assertThat(items.size(), equalTo(1));
    }

    @Test
    void getItemsByRequestIdList_andReturns_1() {
        List<Item> items = itemRepository.getItemsByRequestIdList(List.of(1L));

        assertThat(items.size(), equalTo(1));
    }
}
