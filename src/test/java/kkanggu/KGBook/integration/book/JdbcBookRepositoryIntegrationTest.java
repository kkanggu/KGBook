package kkanggu.KGBook.integration.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.repository.JdbcBookRepository;
import kkanggu.KGBook.common.aws.ImageController;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class JdbcBookRepositoryIntegrationTest {
	@Autowired
	private JdbcBookRepository jdbcBookRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ImageController imageController;

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

	@Test
	@DisplayName("서적 저장 성공")
	void saveBookOk() {
		BookEntity book = getBookEntity(13L, "title");

		Long savedIsbn = jdbcBookRepository.saveBook(book);

		assertAll(
				() -> assertThat(savedIsbn).isNotNull(),
				() -> assertThat(savedIsbn).isEqualTo(book.getIsbn())
		);
		deleteImage();
	}

	@Test
	@DisplayName("전체 서적 가져오기 성공")
	void findAllOk() {
		List<BookEntity> books = new ArrayList<>();
		books.add(getBookEntity(13L, "title"));
		books.add(getBookEntity(135L, "title"));
		books.forEach(jdbcBookRepository::saveBook);

		List<BookEntity> findBooks = jdbcBookRepository.findAll();

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
		List<BookEntity> findBooks = jdbcBookRepository.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 서적 가져오기 성공")
	void findByIsbnOk() {
		BookEntity book = getBookEntity(13L, "title");
		jdbcBookRepository.saveBook(book);

		BookEntity findBook = jdbcBookRepository.findByIsbn(book.getIsbn());

		assertAll(
				() -> assertThat(findBook).isNotNull(),
				() -> assertThat(findBook.getIsbn()).isEqualTo(findBook.getIsbn()),
				() -> assertThat(findBook.getTitle()).isEqualTo(findBook.getTitle())
		);
		deleteImage();
	}

	@Test
	@DisplayName("Isbn을 이용하여 서적 가져오기 성공, 해당 서적이 없을 경우")
	void findByIsbnNotFound() {
		Long isbn = 13L;

		BookEntity findBook = jdbcBookRepository.findByIsbn(isbn);

		assertAll(
				() -> assertThat(findBook).isNull()
		);
	}

	@Test
	@DisplayName("서적 갱신 성공")
	void updateBookOk() {
		Long isbn = 13L;
		BookEntity book = getBookEntity(isbn, "title");
		jdbcBookRepository.saveBook(book);

		jdbcBookRepository.updateBook(getBookEntity(isbn, "changedTitle"));

		BookEntity updatedBook = jdbcBookRepository.findByIsbn(isbn);
		assertAll(
				() -> assertThat(updatedBook).isNotNull(),
				() -> assertThat(updatedBook.getIsbn()).isEqualTo(book.getIsbn()),
				() -> assertThat(updatedBook.getTitle()).isNotEqualTo(book.getTitle())
		);
		deleteImage();
	}
}
