package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.utils.PageConverter;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts = {"file:src/test/resources/test-schema.sql", "file:src/test/resources/test-data.sql"})
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;

    private static final Pageable PAGEABLE = PageConverter.toPageRequest(0, 10)
            .withSort(Sort.Direction.DESC, "created");

    @Test
    void findAllRequests_whereRequesterId_notTheSameUsersId() {
        List<ItemRequest> requests = requestRepository.findAllRequests(1L, PAGEABLE)
                .stream().collect(Collectors.toList());

        assertThat(1, equalTo(requests.size()));
        assertThat("looking for new hummer", equalTo(requests.get(0).getDescription()));
    }

    @Test
    void findAllByRequesterId_andReturns_1() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(3L);

        assertThat(1, equalTo(requests.size()));
        assertThat("looking for new hummer", equalTo(requests.get(0).getDescription()));
    }
}
