package kkanggu.KGBook.book.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.repository.BookOwnerOrderRepository;
import kkanggu.KGBook.book.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
	@InjectMocks
	private BookService bookService;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookOwnerOrderRepository bookOwnerOrderRepository;

	private BookEntity getBookEntity(Long isbn) {
		return BookEntity.builder()
				.isbn(isbn)
				.title("book")
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.now())
				.description("description")
				.s3ImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
	}

	@Test
	@DisplayName("서적 저장")
	void saveBook() {
		BookEntity book = getBookEntity(135L);
		when(bookRepository.saveBook(book)).thenReturn(book.getIsbn());

		Long isbn = bookService.saveBook(book);

		assertThat(isbn).isEqualTo(book.getIsbn());
	}

	@Test
	@DisplayName("전체 서적 가져오기, 서적 존재")
	void findAllExist() {
		List<BookEntity> books = new ArrayList<>();
		books.add(getBookEntity(135L));
		books.add(getBookEntity(1357L));
		books.add(getBookEntity(13579L));
		when(bookRepository.findAll()).thenReturn(books);

		List<BookEntity> findBooks = bookRepository.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.size()).isEqualTo(books.size()),
				() -> IntStream.range(0, findBooks.size())
						.forEach(i -> assertThat(findBooks.get(i)).isEqualTo(books.get(i)))
		);
	}

	@Test
	@DisplayName("전체 서적 가져오기, 서적이 없을 경우")
	void findAllEmpty() {
		List<BookEntity> books = new ArrayList<>();
		when(bookRepository.findAll()).thenReturn(books);

		List<BookEntity> findBooks = bookRepository.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("isbn을 이용하여 서적을 가져옴, 서적 존재")
	void findByIsbnExist() {
		BookEntity book = getBookEntity(13L);
		when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(book);

		BookEntity findBook = bookRepository.findByIsbn(book.getIsbn());

		assertAll(
				() -> assertThat(findBook).isNotNull(),
				() -> assertThat(findBook).isEqualTo(book)
		);
	}

	@Test
	@DisplayName("isbn을 이용하여 서적을 가져옴, 해당 isbn과 동일한 서적이 없을 경우")
	void findByIsbnCantFind() {
		when(bookRepository.findByIsbn(any())).thenReturn(null);

		BookEntity findBook = bookRepository.findByIsbn(123L);

		assertAll(
				() -> assertThat(findBook).isNull()
		);
	}

	@Test
	@DisplayName("특정 User가 소유중인 서적을 가져옴, 서적 존재")
	void findBooksUserOwnExist() {
		long userid = 1L;
		List<Long> isbns = new ArrayList<>();
		isbns.add(135L);
		List<BookEntity> books = new ArrayList<>();
		books.add(getBookEntity(isbns.get(0)));
		when(bookOwnerOrderRepository.findIsbnByUserId(userid)).thenReturn(isbns);
		when(bookRepository.findByIsbn(isbns.get(0))).thenReturn(books.get(0));

		List<BookEntity> findBooks = bookService.findBooksUserOwn(userid);

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.size()).isEqualTo(books.size()),
				() -> IntStream.range(0, findBooks.size())
						.forEach(i -> assertThat(findBooks.get(i)).isEqualTo(books.get(i)))
		);
	}

	@Test
	@DisplayName("특정 User가 소유중인 서적을 가져옴, 유저가 소유중인 서적이 없을 경우")
	void findBooksUserOwnUserHaveNothing() {
		long userid = 1L;
		List<Long> isbns = new ArrayList<>();
		when(bookOwnerOrderRepository.findIsbnByUserId(userid)).thenReturn(isbns);

		List<BookEntity> findBooks = bookService.findBooksUserOwn(userid);

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("특정 User가 소유중인 서적을 가져옴, 유저가 소유중인 서적의 isbn을 이용하더라도 서적을 못 찾을 경우. 해당 상황은 발생해서는 안 됨")
	void findBooksUserOwnCantFindBook() {
		long userid = 1L;
		List<Long> isbns = new ArrayList<>();
		isbns.add(135L);
		when(bookOwnerOrderRepository.findIsbnByUserId(userid)).thenReturn(isbns);
		when(bookRepository.findByIsbn(any())).thenReturn(null);

		List<BookEntity> findBooks = bookService.findBooksUserOwn(userid);

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.size()).isEqualTo(isbns.size()),
				() -> IntStream.range(0, findBooks.size())
						.forEach(i -> assertThat(findBooks.get(i)).isNull())
		);
	}
}
