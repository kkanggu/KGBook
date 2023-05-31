package kkanggu.KGBook.book.service;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.repository.BookRepository;
import kkanggu.KGBook.common.aws.ImageController;


@SpringBootTest
@ActiveProfiles("local")
@Transactional
class BookServiceTest {
	private final BookRepository bookRepository;
	private final BookService bookService;
	private final JdbcTemplate jdbcTemplate;
	private final ImageController imageController;

	@Autowired
	public BookServiceTest(BookRepository bookRepository,
						   BookService bookService,
						   JdbcTemplate jdbcTemplate,
						   ImageController imageController) {
		this.bookRepository = bookRepository;
		this.bookService = bookService;
		this.jdbcTemplate = jdbcTemplate;
		this.imageController = imageController;
	}

	@BeforeEach
	void setup() {
		jdbcTemplate.update("DELETE FROM BOOK");
	}

	void insertBooksBeforeTest() {
		for (int i = 0; i < 4; ++i) {
			BookEntity book = BookEntity.builder()
					.isbn(1357924680130L + i)
					.title("book" + (i + 1))
					.author("author" + (i + 1))
					.publisher("publisher")
					.originPrice(13579)
					.publishDate(LocalDate.now()).createDate(LocalDate.now())
					.description("description")
					.originImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
					.build();
			bookRepository.saveBook(book);
		}
	}

	@Test
	@DisplayName("서적 저장")
	void saveBook() {
		// when
		BookEntity book = BookEntity.builder()
				.isbn(1357924680134L)
				.title("title")
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.now())
				.createDate(LocalDate.now())
				.description("description")
				.originImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();

		// given
		Long isbn = bookService.saveBook(book);

		// then
		assertThat(isbn).isNotNull();
		assertThat(isbn).isEqualTo(book.getIsbn());

		BookEntity savedBook = bookRepository.findByIsbn(isbn);
		boolean isDeleted = imageController.deleteImage(savedBook.getS3ImageUrl());
		assertThat(isDeleted).isEqualTo(true);
	}

	@Test
	@DisplayName("DB에서 서적 정보를 가져옴")
	void findAllTest() {
		// given
		insertBooksBeforeTest();

		// when
		List<BookEntity> books = bookService.findAll();

		// then
		assertThat(books.size()).isEqualTo(4);

		for (BookEntity book : books) {
			boolean isDeleted = imageController.deleteImage(book.getS3ImageUrl());
			assertThat(isDeleted).isEqualTo(true);
		}
	}

	@Test
	@DisplayName("DB에서 isbn을 통해 서적을 가져옴")
	void findByIsbnTest() {
		// given
		BookEntity book = BookEntity.builder()
				.isbn(1357924680134L)
				.title("title")
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.now())
				.createDate(LocalDate.now())
				.description("description")
				.originImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
		bookRepository.saveBook(book);

		// when
		BookEntity findBook = bookService.findByIsbn(book.getIsbn());

		// then
		assertThat(findBook).isNotNull();
		assertThat(book.getIsbn()).isEqualTo(findBook.getIsbn());
		assertThat(book.getTitle()).isEqualTo(findBook.getTitle());
		assertThat(book.getAuthor()).isEqualTo(findBook.getAuthor());
		assertThat(book.getPublisher()).isEqualTo(findBook.getPublisher());
		assertThat(book.getOriginPrice()).isEqualTo(findBook.getOriginPrice());
		assertThat(book.getPublishDate()).isEqualTo(findBook.getPublishDate());
		assertThat(book.getDescription()).isEqualTo(findBook.getDescription());
		assertThat(book.getOriginImageUrl()).isEqualTo(findBook.getOriginImageUrl());
		assertThat(findBook.getS3ImageUrl()).isNotNull();

		boolean isDeleted = imageController.deleteImage(findBook.getS3ImageUrl());
		assertThat(isDeleted).isEqualTo(true);
	}
}
