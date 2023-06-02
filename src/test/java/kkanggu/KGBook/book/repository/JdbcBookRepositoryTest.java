package kkanggu.KGBook.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.common.aws.ImageController;

@ExtendWith(MockitoExtension.class)
class JdbcBookRepositoryTest {
	@InjectMocks
	private JdbcBookRepository repository;

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Mock
	private ImageController imageController;

	private BookEntity getBookEntity(Long isbn) {
		return BookEntity.builder()
				.isbn(isbn)
				.title("book")
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.now())
				.description("description")
				.originImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
	}

	@Test
	@DisplayName("서적 저장 성공")
	void saveBookOk() {
		BookEntity book = getBookEntity(135L);
		when(imageController.uploadImage(anyString())).thenReturn("");
		when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);

		Long isbn = repository.saveBook(book);

		assertAll(
				() -> assertThat(isbn).isNotNull(),
				() -> assertThat(isbn).isEqualTo(book.getIsbn())
		);
	}

	@Test
	@DisplayName("서적 저장 실패, Image URL이 없을 경우")
	void saveBookImageNotExist() {
		BookEntity book = getBookEntity(135L);
		when(imageController.uploadImage(anyString())).thenReturn(null);

		Long isbn = repository.saveBook(book);

		assertAll(
				() -> assertThat(isbn).isNull()
		);
	}

	@Test
	@DisplayName("서적 저장 실패, 모종의 이유로 저장 실패")
	void saveBookImageFail() {
		BookEntity book = getBookEntity(135L);
		when(imageController.uploadImage(anyString())).thenReturn("");
		when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(0);

		Long isbn = repository.saveBook(book);

		assertAll(
				() -> assertThat(isbn).isNull()
		);
	}

	@Test
	@DisplayName("전체 서적 가져오기 성공")
	void findAllOk() {
		List<BookEntity> books = new ArrayList<>();
		BookEntity book = getBookEntity(135L);
		books.add(book);
		when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(books);

		List<BookEntity> findBooks = repository.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.size()).isEqualTo(books.size()),
				() -> IntStream.range(0, findBooks.size())
						.forEach(i -> assertThat(findBooks.get(i)).isEqualTo(books.get(i)))
		);
	}

	@Test
	@DisplayName("전체 서적 가져오기 성공, 아무것도 없을 경우")
	void findAllEmpty() {
		when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList());

		List<BookEntity> findBooks = repository.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 서적 가져오기 성공")
	void findByIsbnOk() {
		BookEntity book = getBookEntity(135L);
		when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any())).thenReturn(book);

		BookEntity findBook = repository.findByIsbn(book.getIsbn());

		assertAll(
				() -> assertThat(findBook).isNotNull(),
				() -> assertThat(findBook).isEqualTo(book)
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 서적 가져오기 실패")
	void findByIsbnFail() {
		Long isbn = 13L;
		when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any())).thenReturn(null);

		BookEntity findBook = repository.findByIsbn(isbn);

		assertAll(
				() -> assertThat(findBook).isNull()
		);
	}
}
