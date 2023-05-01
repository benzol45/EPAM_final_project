-- book
CREATE INDEX indexAuthor
ON book(author);
CREATE INDEX indexTitle
ON book(title);

-- rating
CREATE INDEX indexBookId
ON rating(user_id);
