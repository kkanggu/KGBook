package kkanggu.KGBook.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.sql.BookSql;

@SpringBootTest
@Sql("/sql/book/ddl.sql")
@ActiveProfiles("local")
@Transactional
class JdbcBookRepositoryTest {
	private final JdbcTemplate jdbcTemplate;
	private final JdbcBookRepository jdbcBookRepository;

	@Autowired
	public JdbcBookRepositoryTest(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcBookRepository = new JdbcBookRepository(dataSource);
	}

	@BeforeEach
	void setup() {
		jdbcBookRepository.setId();
	}

	void insertBooksBeforeTest() {
		for (int i = 0; i < 10; ++i) {
			BookEntity book = new BookEntity("book" + (i + 1), "author" + (i + 1), "publisher", LocalDate.now(), "147258" + i, "description");
			jdbcBookRepository.saveBook(book);
		}
	}

	@Test
	@DisplayName("서적 저장")
	void createBookTest() {
		// given

		// when
		BookEntity book = new BookEntity("title", "author", "publisher", LocalDate.now(), "isbn", "description");
		Long id = jdbcBookRepository.saveBook(book);
		BookEntity findBook = jdbcTemplate.queryForObject(BookSql.SELECT_BOOKS_BY_ID, rowMapper(), id);

		// then
		assertThat(findBook).isNotNull();
		assertThat(book.getTitle()).isEqualTo(findBook.getTitle());
		assertThat(book.getAuthor()).isEqualTo(findBook.getAuthor());
		assertThat(book.getPublisher()).isEqualTo(findBook.getPublisher());
		assertThat(book.getPublishDate()).isEqualTo(findBook.getPublishDate());
		assertThat(book.getIsbn()).isEqualTo(findBook.getIsbn());
		assertThat(book.getDescription()).isEqualTo(findBook.getDescription());
	}

	@Test
	@DisplayName("서적 전체 가져오기")
	void findAllTest() {
		// given
		insertBooksBeforeTest();

		// when
		List<BookEntity> books = jdbcBookRepository.findAll();

		// then
		assertThat(books.size()).isEqualTo(10);
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
