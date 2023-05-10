package kkanggu.KGBook.book.repository;

import java.time.LocalDate;
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

	@Autowired
	public JdbcBookRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Long saveBook(BookEntity book) {
		Object[] params = {book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublishDate(), book.getIsbn(), book.getDescription()};
		jdbcTemplate.update(BookSql.CREATE_BOOK, params);
		BookEntity findBook = jdbcTemplate.queryForObject(BookSql.SELECT_BOOKS_BY_ISBN, rowMapper(), book.getIsbn());
		return null == findBook ? null : findBook.getId();
	}

	private RowMapper<BookEntity> rowMapper() {
		return (rs, rowNum) -> {
			BookEntity book = new BookEntity();
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
