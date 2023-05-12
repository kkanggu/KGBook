package kkanggu.KGBook.book.repository;

import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.common.aws.ImageController;
import kkanggu.KGBook.sql.BookSql;

@Repository
public class JdbcBookRepository implements BookRepository {
	private final JdbcTemplate jdbcTemplate;
	private final ImageController imageController;

	@Autowired
	public JdbcBookRepository(DataSource dataSource,
							  ImageController imageController) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.imageController = imageController;
	}

	@Override
	public int saveBook(BookEntity book) {
		String s3ImageUrl = imageController.uploadImage(book.getOriginImageUrl());

		if (null == s3ImageUrl) {
			return 0;
		}

		Object[] params = {book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublishDate(),
				book.getDescription(), book.getOriginImageUrl(), s3ImageUrl};
		int rows = jdbcTemplate.update(BookSql.CREATE_BOOK, params);
		return rows;
	}

	@Override
	public List<BookEntity> findAll() {
		List<BookEntity> books = jdbcTemplate.query(BookSql.SELECT_BOOKS, rowMapper());
		return books;
	}

	@Override
	public BookEntity findByIsbn(Long isbn) {
		BookEntity book = jdbcTemplate.queryForObject(BookSql.SELECT_BOOKS_BY_ISBN, rowMapper(), isbn);
		return book;
	}

	private RowMapper<BookEntity> rowMapper() {
		return (rs, rowNum) -> {
			BookEntity book = new BookEntity();
			book.setIsbn(rs.getLong("isbn"));
			book.setTitle(rs.getString("title"));
			book.setAuthor(rs.getString("author"));
			book.setPublisher(rs.getString("publisher"));
			LocalDate publishDate = rs.getTimestamp("publish_date").toLocalDateTime().toLocalDate();
			book.setPublishDate(publishDate);
			book.setDescription(rs.getString("description"));
			book.setOriginImageUrl(rs.getString("originImageUrl"));
			book.setS3ImageUrl(rs.getString("s3ImageUrl"));

			return book;
		};
	}
}
