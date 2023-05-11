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
	private Long id;

	@Autowired
	public JdbcBookRepository(DataSource dataSource,
							  ImageController imageController) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.imageController = imageController;
	}

	public void setId() {
		id = jdbcTemplate.queryForObject(BookSql.SELECT_MAX_ID, Long.class);
		if (null == id) {
			id = 0L;
		}
	}

	@Override
	public Long saveBook(BookEntity book) {
		String s3ImageUrl = imageController.uploadImage(book.getOriginImageUrl());

		if (null == s3ImageUrl) {
			return null;
		}

		Object[] params = {++id, book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublishDate(),
				book.getIsbn(), book.getDescription(), book.getOriginImageUrl(), s3ImageUrl};
		jdbcTemplate.update(BookSql.CREATE_BOOK, params);
		return id;
	}

	@Override
	public List<BookEntity> findAll() {
		List<BookEntity> books = jdbcTemplate.query(BookSql.SELECT_BOOKS, rowMapper());
		return books;
	}

	private RowMapper<BookEntity> rowMapper() {
		return (rs, rowNum) -> {
			BookEntity book = new BookEntity();
			book.setId(rs.getLong("id"));
			book.setTitle(rs.getString("title"));
			book.setAuthor(rs.getString("author"));
			book.setPublisher(rs.getString("publisher"));
			LocalDate publishDate = rs.getTimestamp("publish_date").toLocalDateTime().toLocalDate();
			book.setPublishDate(publishDate);
			book.setIsbn(rs.getString("isbn"));
			book.setDescription(rs.getString("description"));
			book.setOriginImageUrl(rs.getString("originImageUrl"));
			book.setS3ImageUrl(rs.getString("s3ImageUrl"));

			return book;
		};
	}
}
