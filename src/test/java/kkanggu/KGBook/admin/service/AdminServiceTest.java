package kkanggu.KGBook.admin.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.admin.dto.ApiBookDto;
import kkanggu.KGBook.book.controller.BookController;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.common.aws.ImageController;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class AdminServiceTest {
	private final AdminService adminService;
	private final BookController bookController;
	private final ImageController imageController;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public AdminServiceTest(AdminService adminService,
							BookController bookController,
							ImageController imageController,
							JdbcTemplate jdbcTemplate) {
		this.adminService = adminService;
		this.bookController = bookController;
		this.imageController = imageController;
		this.jdbcTemplate = jdbcTemplate;
	}

	@BeforeEach
	void setup() {
		jdbcTemplate.update("DELETE FROM BOOK");
	}

	@Test
	@DisplayName("Naver API를 이용하여 서적 정보를 문자열로 가져옴 - 정확도")
	void fetchBookFromNaverApiAccuracy() {
		// given
		String keyword = "자바";

		// when
		ResponseEntity<String> apiResponseEntity = adminService.fetchBookFromNaverApi(keyword, false);

		// then
		assertThat(apiResponseEntity.getStatusCode().is2xxSuccessful()).isEqualTo(true);
	}

	@Test
	@DisplayName("Naver API를 이용하여 서적 정보를 문자열로 가져옴 - 최신")
	void fetchBookFromNaverApiRecent() {
		// given
		String keyword = "자바";

		// when
		ResponseEntity<String> apiResponseEntity = adminService.fetchBookFromNaverApi(keyword, true);

		// then
		assertThat(apiResponseEntity.getStatusCode().is2xxSuccessful()).isEqualTo(true);

		List<String> pubdates = new ArrayList<>();
		JSONArray jsonArray = XML.toJSONObject(apiResponseEntity.getBody())
				.getJSONObject("rss")
				.getJSONObject("channel")
				.getJSONArray("item");
		for (Object object : jsonArray) {
			JSONObject jsonObject = (JSONObject) object;
			pubdates.add(jsonObject.get("pubdate").toString());
		}
		List<String> sortedPubDates = new ArrayList<>(pubdates);
		sortedPubDates.sort(Collections.reverseOrder());

		assertThat(pubdates).isEqualTo(sortedPubDates);
	}

	@Test
	@DisplayName("Xml을 ApiBookDto로 변환")
	void convertXmlToApiBookDto() throws IOException {
		// given
		String booksXml = Files.readString(Path.of("src", "test", "resources", "api-string-data.xml"));

		// when
		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(booksXml);

		// then
		assertThat(apiBookDtos).isNotNull();
		assertThat(apiBookDtos.size()).isEqualTo(10);
	}

	@Test
	@DisplayName("ApiBookDto를 BookEntity로 변환")
	void convertApiBookDtoToBookEntity() throws IOException {
		// given
		String booksXml = Files.readString(Path.of("src", "test", "resources", "api-string-data.xml"));
		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(booksXml);

		// when
		List<BookEntity> books = adminService.convertApiBookDtoToBookEntity(apiBookDtos);

		// then
		assertThat(books).isNotNull();
		assertThat(books.size()).isEqualTo(10);
	}

	@Test
	@DisplayName("List<BookEntity>를 저장")
	void saveBooks() {
		// given
		List<BookEntity> books = new ArrayList<>();
		for (int i = 0; i < 4; ++i) {
			BookEntity book = BookEntity.builder()
					.isbn(1357924680130L + i)
					.title("book" + (i + 1))
					.author("author" + (i + 1))
					.publisher("publisher")
					.publishDate(LocalDate.now())
					.createDate(LocalDate.now())
					.description("description")
					.originImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
					.build();
			books.add(book);
		}

		// when
		adminService.saveBooks(books);

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
