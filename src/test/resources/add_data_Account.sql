INSERT INTO users(full_name, contact, login, password, is_blocked, role)
VALUES ('reader','','reader','',false,'READER');
INSERT INTO users(full_name, contact, login, password, is_blocked, role)
VALUES ('librarian','','librarian','',false,'LIBRARIAN');

INSERT INTO book(isbn, author, title, pages, publisher, date_of_publication, quantity)
VALUES ('1','a','title 1',1,'p',null,5);
INSERT INTO book(isbn, author, title, pages, publisher, date_of_publication, quantity)
VALUES ('2','b','title 2',1,'p',null,5);
INSERT INTO book(isbn, author, title, pages, publisher, date_of_publication, quantity)
VALUES ('3','c','title 3',1,'p',null,5);
INSERT INTO book(isbn, author, title, pages, publisher, date_of_publication, quantity)
VALUES ('4','d','title 4',1,'p',null,5);

INSERT INTO orders(user_id, book_id, create_date)
VALUES (2,1,current_timestamp);
INSERT INTO orders(user_id, book_id, create_date)
VALUES (2,2,current_timestamp);

INSERT INTO given_book(user_id, book_id, given_date, return_date)
VALUES (2,3,current_timestamp,current_timestamp);
INSERT INTO given_book(user_id, book_id, given_date, return_date)
VALUES (2,4,current_timestamp,current_timestamp);