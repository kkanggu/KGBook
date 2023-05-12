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
