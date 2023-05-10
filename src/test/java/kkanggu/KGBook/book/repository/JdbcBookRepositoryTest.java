package kkanggu.KGBook.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.sql.BookSql;

@SpringBootTest
@Sql("/sql/book/ddl.sql")
@ActiveProfiles("local")
class JdbcBookRepositoryTest {
	private final JdbcTemplate jdbcTemplate;
	private final JdbcBookRepository jdbcBookRepository;

	@Autowired
	public JdbcBookRepositoryTest(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcBookRepository = new JdbcBookRepository(dataSource);
	}

	@Test
	@DisplayName("서적 저장")
	void createBookTest() {
		// given

		// when
		BookEntity book = new BookEntity("title", "author", "publisher", LocalDate.now(), "isbn", "description");
		jdbcBookRepository.saveBook(book);
		BookEntity findBook = jdbcTemplate.queryForObject(BookSql.SELECT_BOOKS_BY_ISBN, rowMapper(), book.getIsbn());

		// then
		assertThat(findBook).isNotNull();
		assertThat(book.getTitle()).isEqualTo(findBook.getTitle());
		assertThat(book.getAuthor()).isEqualTo(findBook.getAuthor());
		assertThat(book.getPublisher()).isEqualTo(findBook.getPublisher());
		assertThat(book.getPublishDate()).isEqualTo(findBook.getPublishDate());
		assertThat(book.getIsbn()).isEqualTo(findBook.getIsbn());
		assertThat(book.getDescription()).isEqualTo(findBook.getDescription());
	}

	RowMapper<BookEntity> rowMapper() {
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
