package kkanggu.KGBook.integration.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.repository.BookOwnerOrderRepository;
import kkanggu.KGBook.book.service.BookService;
import kkanggu.KGBook.common.aws.ImageController;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.service.UserService;

@SpringBootTest
@Transactional
public class BookServiceIntegrationTest {
	@Autowired
	private BookService bookService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ImageController imageController;

	@Autowired
	private UserService userService;

	@Autowired
	private BookOwnerOrderRepository bookOwnerOrderRepository;

	@BeforeEach
	void init() {
		jdbcTemplate.update("DELETE FROM BOOK");
	}

	private void deleteImage() {
		List<String> s3ImageUrls = jdbcTemplate.query("SELECT s3_image_url FROM BOOK", (rs, rowNum) -> rs.getString("s3_image_url"));
		for (String s3ImageUrl : s3ImageUrls) {
			imageController.deleteImage(s3ImageUrl);
		}
	}

	private BookEntity getBookEntity(Long isbn, String title) {
		return BookEntity.builder()
				.isbn(isbn)
				.title(title)
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.of(2016, 1, 1))
				.createDate(LocalDate.now())
				.description("description")
				.originImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
	}

	private UserEntity getUserEntity() {
		return UserEntity.builder()
				.id(null)
				.username("user")
				.password("password")
				.gender("M")
				.age(1)
				.birth(LocalDate.of(2000, 1, 1))
				.createDate(LocalDate.now())
				.build();
	}

	@Test
	@DisplayName("서적 저장 성공")
	void saveBookOk() {
		BookEntity book = getBookEntity(13L, "title");

		Long savedId = bookService.saveBook(book);

		assertAll(
				() -> assertThat(savedId).isNotNull(),
				() -> assertThat(savedId).isEqualTo(book.getIsbn())
		);
		deleteImage();
	}

	@Test
	@DisplayName("서적 저장 실패")
	void saveBookFail() {
		BookEntity book = getBookEntity(13L, "title");
		bookService.saveBook(book);

		assertAll(
				() -> assertThrows(RuntimeException.class, () -> bookService.saveBook(book))
		);
		deleteImage();
	}

	@Test
	@DisplayName("전체 서적 가져오기 성공")
	void findAllOk() {
		List<BookEntity> books = new ArrayList<>();
		books.add(getBookEntity(13L, "title"));
		books.add(getBookEntity(135L, "title"));
		books.forEach(bookService::saveBook);

		List<BookEntity> findBooks = bookService.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.size()).isEqualTo(books.size()),
				() -> IntStream.range(0, findBooks.size())
						.forEach(i -> assertThat(findBooks.get(i).getIsbn()).isEqualTo(books.get(i).getIsbn()))
		);
		deleteImage();
	}

	@Test
	@DisplayName("전체 서적 가져오기 성공, 서적이 없을 경우")
	void findAllEmpty() {
		List<BookEntity> findBooks = bookService.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 서적 가져오기 성공")
	void findByIsbnOk() {
		BookEntity book = getBookEntity(13L, "title");
		bookService.saveBook(book);

		BookEntity findBook = bookService.findByIsbn(book.getIsbn());

		assertAll(
				() -> assertThat(findBook).isNotNull(),
				() -> assertThat(findBook.getIsbn()).isEqualTo(book.getIsbn())
		);
		deleteImage();
	}

	@Test
	@DisplayName("Isbn을 이용하여 서적 가져오기 실패, 해당 서적이 없을 경우")
	void findByIsbnFail() {
		Long isbn = 13L;

		BookEntity findBook = bookService.findByIsbn(isbn);

		assertAll(
				() -> assertThat(findBook).isNull()
		);
		deleteImage();
	}

	@Test
	@DisplayName("유저가 소유중인 서적 가져오기 성공")
	void findBooksUserOwnOk() {
		BookEntity book1 = getBookEntity(1L, "title1");
		BookEntity book2 = getBookEntity(2L, "title2");
		bookService.saveBook(book1);
		bookService.saveBook(book2);
		Long userId = userService.saveUser(getUserEntity());
		bookOwnerOrderRepository.saveBookUserOwn(book1.getIsbn(), userId);
		bookOwnerOrderRepository.saveBookUserOwn(book2.getIsbn(), userId);

		List<BookEntity> findBooks = bookService.findBooksUserOwn(userId);

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.stream()
						.map(BookEntity::getIsbn)
						.toList()).contains(book1.getIsbn(), book2.getIsbn())
		);
		deleteImage();
	}

	@Test
	@DisplayName("유저가 소유중인 서적 가져오기 실패, 유저나 서적이 없을 경우")
	void findBooksUserOwnEmpty() {
		Long userId = 13L;

		List<BookEntity> findBooks = bookService.findBooksUserOwn(userId);

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("서적 갱신 성공")
	void updateBookOk() {
		Long isbn = 13L;
		BookEntity book = getBookEntity(isbn, "title");
		bookService.saveBook(book);

		bookService.updateBook(getBookEntity(isbn, "changedTitle"));

		BookEntity updatedBook = bookService.findByIsbn(isbn);
		assertAll(
				() -> assertThat(updatedBook).isNotNull(),
				() -> assertThat(updatedBook.getIsbn()).isEqualTo(book.getIsbn()),
				() -> assertThat(updatedBook.getTitle()).isNotEqualTo(book.getTitle())
		);
		deleteImage();
	}
}
