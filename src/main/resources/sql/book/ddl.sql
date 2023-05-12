CREATE TABLE IF NOT EXISTS BOOK (
  isbn VARCHAR(20) NOT NULL PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  author VARCHAR(100) NOT NULL,
  publisher VARCHAR(100) NOT NULL,
  publish_date TIMESTAMP NOT NULL,
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
  birth VARCHAR(8),
  create_date TIMESTAMP NOT NULL
);
