package kkanggu.KGBook.book.repository;

import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.sql.BookSql;

@Repository
public class JdbcBookRepository implements BookRepository {
	private final JdbcTemplate jdbcTemplate;
	private Long id;

	@Autowired
	public JdbcBookRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void setId() {
		id = jdbcTemplate.queryForObject(BookSql.SELECT_MAX_ID, Long.class);
		if (null == id) {
			id = 0L;
		}
	}

	@Override
	public Long saveBook(BookEntity book) {
		Object[] params = {++id, book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublishDate(), book.getIsbn(), book.getDescription()};
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

			return book;
		};
	}
}
