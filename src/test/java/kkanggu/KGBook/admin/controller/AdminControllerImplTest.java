package kkanggu.KGBook.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.controller.BookController;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.common.aws.ImageController;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class AdminControllerImplTest {
	private final AdminController adminController;
	private final BookController bookController;
	private final ImageController imageController;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public AdminControllerImplTest(AdminController adminController,
								   BookController bookController,
								   ImageController imageController,
								   JdbcTemplate jdbcTemplate) {
		this.adminController = adminController;
		this.bookController = bookController;
		this.imageController = imageController;
		this.jdbcTemplate = jdbcTemplate;
	}

	@BeforeEach
	void setup() {
		jdbcTemplate.update("DELETE FROM BOOK");
	}

	@Test
	@DisplayName("Naver API를 이용하여 서적을 검색 - 정확도")
	void searchBooksAccuracy() {
		// given
		String keyword = "레디스";

		// when
		List<BookEntity> books = adminController.searchBooks(keyword, false);

		// then
		assertThat(books).isNotNull();
		assertThat(books.size()).isNotEqualTo(0);
	}

	@Test
	@DisplayName("Naver API를 이용하여 서적을 검색 - 최신")
	void searchBooksRecent() {
		// given
		String keyword = "레디스";

		// when
		List<BookEntity> books = adminController.searchBooks(keyword, true);

		// then
		assertThat(books).isNotNull();
		assertThat(books.size()).isNotEqualTo(0);

		List<LocalDate> bookPublishDates = books.stream()
				.map(BookEntity::getPublishDate)
				.toList();

		List<LocalDate> sortedPublishDates = new ArrayList<>(bookPublishDates);
		sortedPublishDates.sort(Collections.reverseOrder());

		assertThat(bookPublishDates).isEqualTo(sortedPublishDates);
	}

	@Test
	void saveBooks() {
		// given
		List<BookEntity> books = new ArrayList<>();
		for (int i = 0; i < 4; ++i) {
			BookEntity book = new BookEntity(1357924680130L + i, "book" + (i + 1), "author" + (i + 1), "publisher", LocalDate.now(),
					"description", "https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg", null);
			books.add(book);
		}

		// when
		adminController.saveBooks(books);

		// then
		List<BookEntity> findBooks = bookController.findAll();
		assertThat(findBooks).isNotNull();
		assertThat(findBooks.size()).isEqualTo(4);

		for (BookEntity book : findBooks) {
			boolean isDeleted = imageController.deleteImage(book.getS3ImageUrl());
			assertThat(isDeleted).isEqualTo(true);
		}
	}
}
