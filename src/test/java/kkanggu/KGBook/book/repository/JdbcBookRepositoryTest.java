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
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.common.aws.ImageController;
import kkanggu.KGBook.sql.BookSql;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class JdbcBookRepositoryTest {
	private final JdbcTemplate jdbcTemplate;
	private final JdbcBookRepository jdbcBookRepository;
	private final ImageController imageController;

	@Autowired
	public JdbcBookRepositoryTest(DataSource dataSource,
								  ImageController imageController) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcBookRepository = new JdbcBookRepository(dataSource, imageController);
		this.imageController = imageController;
	}

	void insertBooksBeforeTest() {
		for (int i = 0; i < 4; ++i) {
			BookEntity book = new BookEntity(1357924680130L + i, "book" + (i + 1), "author" + (i + 1), "publisher",
					LocalDate.now(), LocalDate.now(),"description",
					"https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg", null);
			jdbcBookRepository.saveBook(book);
		}
	}

	@BeforeEach
	void clear() {
		jdbcTemplate.update("DELETE FROM BOOK");
	}

	@Test
	@DisplayName("서적 저장")
	void saveBookTest() {
		// given
		BookEntity book = new BookEntity(1357924680134L, "title", "author", "publisher",
				LocalDate.now(), LocalDate.now(), "description",
				"https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg", null);

		// when
		long isbn = jdbcBookRepository.saveBook(book);
		BookEntity findBook = jdbcTemplate.queryForObject(BookSql.SELECT_BOOKS_BY_ISBN, rowMapper(), book.getIsbn());

		// then
		assertThat(isbn).isEqualTo(book.getIsbn());
		assertThat(findBook).isNotNull();
		assertThat(book.getIsbn()).isEqualTo(findBook.getIsbn());
		assertThat(book.getTitle()).isEqualTo(findBook.getTitle());
		assertThat(book.getAuthor()).isEqualTo(findBook.getAuthor());
		assertThat(book.getPublisher()).isEqualTo(findBook.getPublisher());
		assertThat(book.getPublishDate()).isEqualTo(findBook.getPublishDate());
		assertThat(book.getDescription()).isEqualTo(findBook.getDescription());
		assertThat(book.getOriginImageUrl()).isEqualTo(findBook.getOriginImageUrl());
		assertThat(findBook.getS3ImageUrl()).isNotNull();

		boolean isDeleted = imageController.deleteImage(findBook.getS3ImageUrl());
		assertThat(isDeleted).isEqualTo(true);
	}

	@Test
	@DisplayName("서적 전체 가져오기")
	void findAllTest() {
		// given
		insertBooksBeforeTest();

		// when
		List<BookEntity> books = jdbcBookRepository.findAll();

		// then
		assertThat(books.size()).isEqualTo(4);

		for (BookEntity book : books) {
			boolean isDeleted = imageController.deleteImage(book.getS3ImageUrl());
			assertThat(isDeleted).isEqualTo(true);
		}
	}

	@Test
	@DisplayName("isbn을 이용하여 서적 가져오기")
	void findByIsbnTest() {
		// given
		BookEntity book = new BookEntity(1357924680134L, "title", "author", "publisher",
				LocalDate.now(), LocalDate.now(),"description",
				"https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg", null);
		Long isbn = jdbcBookRepository.saveBook(book);

		// when
		BookEntity findBook = jdbcBookRepository.findByIsbn(book.getIsbn());

		// then
		assertThat(isbn).isEqualTo(book.getIsbn());
		assertThat(findBook).isNotNull();
		assertThat(findBook.getPublishDate()).isEqualTo(book.getPublishDate());

		boolean isDeleted = imageController.deleteImage(findBook.getS3ImageUrl());
		assertThat(isDeleted).isEqualTo(true);
	}

	RowMapper<BookEntity> rowMapper() {
		return (rs, rowNum) -> {
			BookEntity book = new BookEntity();
			book.setIsbn(rs.getLong("isbn"));
			book.setTitle(rs.getString("title"));
			book.setAuthor(rs.getString("author"));
			book.setPublisher(rs.getString("publisher"));
			LocalDate publishDate = rs.getTimestamp("publish_date").toLocalDateTime().toLocalDate();
			book.setPublishDate(publishDate);
			book.setDescription(rs.getString("description"));
			book.setOriginImageUrl(rs.getString("original_image_url"));
			book.setS3ImageUrl(rs.getString("s3_image_url"));

			return book;
		};
	}
}
