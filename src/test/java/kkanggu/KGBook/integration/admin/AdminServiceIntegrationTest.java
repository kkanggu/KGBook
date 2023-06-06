package kkanggu.KGBook.integration.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.admin.dto.ApiBookDto;
import kkanggu.KGBook.admin.service.AdminService;
import kkanggu.KGBook.book.dto.RenderBookDto;
import kkanggu.KGBook.common.aws.ImageController;

@SpringBootTest
@Transactional
public class AdminServiceIntegrationTest {
	@Autowired
	private AdminService adminService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ImageController imageController;

	private RenderBookDto getRenderBookDto(Long isbn) {
		return RenderBookDto.builder()
				.isbn(isbn)
				.title("book")
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.now())
				.description("description")
				.imageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
	}

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

	@Test
	@DisplayName("Naver API 호출 성공, 정확도순")
	void fetchBookFromNaverApiNotRecentOk() {
		String keyword = "자바";
		boolean searchRecent = false;

		ResponseEntity<String> responseEntity = adminService.fetchBookFromNaverApi(keyword, searchRecent);

		assertAll(
				() -> assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue()
		);
	}

	@Test
	@DisplayName("Naver API 호출 성공, 최신순")
	void fetchBookFromNaverApiRecentOk() {
		String keyword = "자바";
		boolean searchRecent = true;

		ResponseEntity<String> responseEntity = adminService.fetchBookFromNaverApi(keyword, searchRecent);

		assertAll(
				() -> assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue()
		);
	}

	@Test
	@DisplayName("신규 서적 검색 성공, 정확도순")
	void fetchNewBookNotRecentOk() {
		String keyword = "자바";
		boolean searchRecent = true;

		ResponseEntity<String> responseEntity = adminService.fetchBookFromNaverApi(keyword, searchRecent);
		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(responseEntity.getBody());
		List<RenderBookDto> renderBookDtos = adminService.convertApiBookDtoToRenderBookDto(apiBookDtos);

		assertAll(
				() -> assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue(),
				() -> assertThat(apiBookDtos).isNotNull(),
				() -> assertThat(renderBookDtos).isNotNull()
		);
	}

	@Test
	@DisplayName("신규 서적 검색 성공, 최신순")
	void fetchNewBookRecentOk() {
		String keyword = "자바";
		boolean searchRecent = true;

		ResponseEntity<String> responseEntity = adminService.fetchBookFromNaverApi(keyword, searchRecent);
		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(responseEntity.getBody());
		List<RenderBookDto> renderBookDtos = adminService.convertApiBookDtoToRenderBookDto(apiBookDtos);

		assertAll(
				() -> assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue(),
				() -> assertThat(apiBookDtos).isNotNull(),
				() -> assertThat(renderBookDtos).isNotNull(),
				() -> IntStream.range(0, renderBookDtos.size() - 1)
						.noneMatch(i -> renderBookDtos.get(i).getPublishDate().isBefore(renderBookDtos.get(i + 1).getPublishDate()))
		);
	}

	@Test
	@DisplayName("신규 서적 검색 성공, 검색 결과가 없을 경우")
	void fetchNewBookEmpty() {
		String keyword = "asdfqwer";
		boolean searchRecent = true;

		ResponseEntity<String> responseEntity = adminService.fetchBookFromNaverApi(keyword, searchRecent);
		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(responseEntity.getBody());
		List<RenderBookDto> renderBookDtos = adminService.convertApiBookDtoToRenderBookDto(apiBookDtos);

		assertAll(
				() -> assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue(),
				() -> assertThat(apiBookDtos).isNotNull(),
				() -> assertThat(apiBookDtos.isEmpty()).isTrue(),
				() -> assertThat(renderBookDtos).isNotNull(),
				() -> assertThat(renderBookDtos.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("서적 저장 성공")
	void saveBookOk() {
		List<RenderBookDto> books = new ArrayList<>();
		books.add(getRenderBookDto(13L));
		books.add(getRenderBookDto(135L));

		adminService.saveBooks(books);

		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(1) FROM BOOK", Integer.class)).isEqualTo(books.size());
		deleteImage();
	}

	@Test
	@DisplayName("전체 서적 가져오기 성공")
	void findAllOK() {
		List<RenderBookDto> books = new ArrayList<>();
		books.add(getRenderBookDto(13L));
		books.add(getRenderBookDto(135L));
		adminService.saveBooks(books);

		List<RenderBookDto> findBooks = adminService.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.size()).isEqualTo(books.size()),
				() -> assertThat(findBooks).isNotNull(),
				() -> IntStream.range(0, findBooks.size())
						.forEach(i -> assertThat(findBooks.get(i).getIsbn()).isEqualTo(books.get(i).getIsbn()))
		);
		deleteImage();
	}

	@Test
	@DisplayName("전체 서적 가져오기 성공, 서적이 없을 경우")
	void findAllEmpty() {
		List<RenderBookDto> findBooks = adminService.findAll();

		assertAll(
				() -> assertThat(findBooks).isNotNull(),
				() -> assertThat(findBooks.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 서적 가져오기 성공")
	void findByIsbnOk() {
		Long isbn = 13L;
		List<RenderBookDto> books = new ArrayList<>();
		RenderBookDto book = getRenderBookDto(isbn);
		books.add(book);
		adminService.saveBooks(books);

		RenderBookDto findBook = adminService.findByIsbn(isbn);

		assertAll(
				() -> assertThat(findBook).isNotNull(),
				() -> assertThat(findBook.getIsbn()).isEqualTo(book.getIsbn())
		);
		deleteImage();
	}

	@Test
	@DisplayName("Isbn을 이용하여 서적 가져오기 성공, 해당 isbn의 서적이 없을 경우")
	void findByIsbnNotFound() {
		Long isbn = 13L;

		RenderBookDto findBook = adminService.findByIsbn(isbn);

		assertAll(
				() -> assertThat(findBook).isNull()
		);
	}

	@Test
	@DisplayName("서적 갱신 성공")
	void updateBookOk() {
		Long isbn = 13L;
		List<RenderBookDto> books = new ArrayList<>();
		RenderBookDto book = getRenderBookDto(isbn);
		books.add(book);
		adminService.saveBooks(books);

		book.setTitle("ASDF");
		adminService.updateBook(book);

		String updatedTitle = jdbcTemplate.queryForObject("SELECT title FROM BOOK WHERE isbn = ?", String.class, isbn);

		assertAll(
				() -> assertThat(updatedTitle).isNotNull(),
				() -> assertThat(updatedTitle).isEqualTo(book.getTitle())
		);
		deleteImage();
	}
}
