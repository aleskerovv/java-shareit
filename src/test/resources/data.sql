--users data
insert into users(name, email)
values ('test_user1', 'user1@email.ru');
insert into users(name, email)
values ('test_user2', 'user2@email.ru');
insert into users(name, email)
values ('test_user3', 'user3@email.ru');
--items data
insert into items(item_name, description, available, owner_id, request_id)
values ('item1', 'desc to item1', true, 1, null);
insert into items(item_name, description, available, owner_id, request_id)
values ('item2', 'desc to item2', false, 1, null);
insert into items(item_name, description, available, owner_id, request_id)
values ('item3', 'desc to item3', true, 2, null);
--bookings data
insert into bookings(start_date, end_date, item_id, booker_id, status)
values ('2022-12-03 13:22:22', '2022-12-04 13:22:22', 1, 3, 'APPROVED');
insert into bookings(start_date, end_date, item_id, booker_id, status)
values ('2022-12-08 13:22:22', '2022-12-10 13:22:22', 2, 2, 'WAITING');
insert into bookings(start_date, end_date, item_id, booker_id, status)
values ('2022-12-09 13:22:22', '2022-12-11 13:22:22', 3, 1, 'WAITING');
insert into bookings(start_date, end_date, item_id, booker_id, status)
values ('2022-12-25 13:22:22', '2022-12-26 13:22:22', 1, 2, 'WAITING');
insert into bookings(start_date, end_date, item_id, booker_id, status)
values ('2022-12-15 13:22:22', '2022-12-17 13:22:22', 2, 3, 'WAITING');
--comments data
insert into comments(text, item_id, author_id, created)
values ('awesome item and owner', 1, 3, '2022-12-07 15:30:00')