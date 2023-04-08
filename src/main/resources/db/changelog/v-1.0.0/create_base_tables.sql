CREATE TABLE book (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    isbn varchar UNIQUE,
    author varchar,
    title varchar NOT NULL,
    publisher varchar,
    date_of_publication date,
    quantity int CHECK (quantity>0)
);

CREATE TABLE users (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    full_name varchar NOT NULL,
    contact varchar,
    language varchar NOT NULL DEFAULT 'EN',
    login varchar NOT NULL UNIQUE,
    password varchar NOT NULL,
    is_blocked boolean DEFAULT FALSE,
    role varchar NOT NULL
);

CREATE TABLE issued_book (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id bigint REFERENCES users(id) NOT NULL,
    book_id bigint REFERENCES book(id) NOT NULL,
    return_date timestamp NOT NULL,
    in_reading_room boolean DEFAULT FALSE
);

CREATE TABLE orders (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id bigint REFERENCES users(id) NOT NULL,
    book_id bigint REFERENCES book(id) NOT NULL,
    create_date timestamp NOT NULL
);

CREATE TABLE fine (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id bigint REFERENCES users(id) NOT NULL,
    book_id bigint REFERENCES book(id) NOT NULL,
    amount numeric NOT NULL CHECK (amount>0),
    create_date timestamp NOT NULL
);
