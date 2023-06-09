CREATE TABLE book (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    isbn varchar UNIQUE,
    author varchar,
    title varchar NOT NULL,
    pages int CHECK (pages>0),
    image_path varchar DEFAULT '',
    publisher varchar,
    date_of_publication date,
    quantity int CHECK (quantity>0),
    rating float8 default 0
);

CREATE TABLE users (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    full_name varchar NOT NULL,
    contact varchar,
    login varchar NOT NULL UNIQUE,
    password varchar NOT NULL,
    is_blocked boolean DEFAULT FALSE,
    role varchar NOT NULL DEFAULT 'NA'
);

CREATE TABLE given_book (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id bigint REFERENCES users(id) ON DELETE RESTRICT NOT NULL,
    book_id bigint REFERENCES book(id)  ON DELETE RESTRICT NOT NULL,
    given_date timestamp NOT NULL,
    return_date timestamp NOT NULL,
    in_reading_room boolean DEFAULT FALSE
);

CREATE TABLE orders (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id bigint REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    book_id bigint REFERENCES book(id) ON DELETE CASCADE  NOT NULL,
    create_date timestamp NOT NULL
);

CREATE TABLE rating (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id bigint REFERENCES users (id) ON DELETE SET NULL,
    book_id bigint REFERENCES book (id) ON DELETE CASCADE  NOT NULL,
    rate integer DEFAULT NULL,
    create_date timestamp NOT NULL,
    rate_date timestamp DEFAULT NULL
);
