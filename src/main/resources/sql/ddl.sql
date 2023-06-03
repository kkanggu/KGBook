CREATE TABLE IF NOT EXISTS BOOK (
  isbn BIGINT NOT NULL PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  author VARCHAR(100) NOT NULL,
  publisher VARCHAR(100) NOT NULL,
  original_price INT NOT NULL,
  discount_price INT,
  discount_rate INT,
  discount_type VARCHAR(40),
  publish_date TIMESTAMP NOT NULL,
  create_date TIMESTAMP NOT NULL,
  description TEXT,
  original_image_url VARCHAR(100),
  s3_image_url VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS USER (
  id BIGINT NOT NULL PRIMARY KEY,
  username VARCHAR(20) NOT NULL,
  password VARCHAR(60) NOT NULL,
  gender CHAR(1),
  age INT,
  birth TIMESTAMP,
  create_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS BOOK_USER_OWN (
  isbn BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  FOREIGN KEY (isbn) REFERENCES BOOK(isbn),
  FOREIGN KEY (user_id) REFERENCES USER(id)
);
