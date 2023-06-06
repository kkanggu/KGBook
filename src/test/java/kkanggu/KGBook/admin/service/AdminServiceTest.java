package kkanggu.KGBook.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import kkanggu.KGBook.admin.dto.ApiBookDto;
import kkanggu.KGBook.book.dto.RenderBookDto;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.service.BookService;
import kkanggu.KGBook.common.Keys;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
	@InjectMocks
	private AdminService adminService;

	@Mock
	private BookService bookService;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private Keys keys;

	@Mock
	private RestTemplate restTemplate;

	@Captor
	private ArgumentCaptor<BookEntity> bookEntityArgumentCaptor;

	private RenderBookDto getRenderBookDto() {
		return RenderBookDto.builder()
				.isbn(1357924680130L)
				.title("book")
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.now())
				.description("description")
				.imageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
	}

	private BookEntity getBookEntity() {
		return BookEntity.builder()
				.isbn(1357924680130L)
				.title("book")
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.now())
				.description("description")
				.s3ImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
	}

	private ApiBookDto getApiBookDto() {
		ApiBookDto apiBookDto = new ApiBookDto();
		apiBookDto.setTitle("book");
		apiBookDto.setLink("https://search.naver.com");
		apiBookDto.setImage("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg");
		apiBookDto.setAuthor("author");
		apiBookDto.setDiscount(13579);
		apiBookDto.setPublisher("publisher");
		apiBookDto.setPubdate("20230530");
		apiBookDto.setIsbn(1357924680130L);
		apiBookDto.setDescription("description");

		return apiBookDto;
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	@DisplayName("Naver API 호출 성공")
	void fetchBookFromNaverApiOk(boolean searchRecent) throws IOException {
		String booksXml = Files.readString(Path.of("src", "test", "resources", "api-string-data.xml"));
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
				.thenReturn(ResponseEntity.ok(booksXml));

		ResponseEntity<String> response = adminService.fetchBookFromNaverApi("keyword", searchRecent);

		assertAll(
				() -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
				() -> assertThat(response.getBody()).isEqualTo(booksXml)
		);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	@DisplayName("Naver API 호출 실패")
	void fetchBookFromNaverApiFail(boolean searchRecent) throws IOException {
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
				.thenReturn(ResponseEntity.badRequest().build());

		ResponseEntity<String> response = adminService.fetchBookFromNaverApi("keyword", searchRecent);

		assertAll(
				() -> assertThat(response.getStatusCode().is2xxSuccessful()).isFalse()
		);
	}

	@Test
	@DisplayName("XML을 ApiBooDto로 변환 성공")
	void convertXmlToApiBookDtoOk() throws IOException {
		String booksXml = Files.readString(Path.of("src", "test", "resources", "api-string-data.xml"));

		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(booksXml);

		assertAll(
				() -> assertThat(apiBookDtos).isNotNull(),
				() -> assertThat(apiBookDtos.isEmpty()).isFalse()
		);
	}

	@Test
	@DisplayName("XML을 ApiBooDto로 변환 실패")
	void convertXmlToApiBookDtoFail() throws IOException {
		String booksXml = "<Fail>";
		booksXml += Files.readString(Path.of("src", "test", "resources", "api-string-data.xml"));

		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(booksXml);

		assertAll(
				() -> assertThat(apiBookDtos).isNotNull(),
				() -> assertThat(apiBookDtos.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("검색 결과가 아무것도 없을 경우")
	void convertXmlToApiBookDtoNoItem() {
		String booksXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel>" +
				"<title>Naver Open API - book_adv ::&apos;&apos;</title><link>https://search.naver.com</link>" +
				"<description>Naver Search Result</description><lastBuildDate>Mon, 29 May 2023 23:40:54 +0900" +
				"</lastBuildDate><total>0</total><start>1</start><display>0</display></channel></rss>";

		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(booksXml);

		assertAll(
				() -> assertThat(apiBookDtos).isNotNull(),
				() -> assertThat(apiBookDtos.isEmpty()).isTrue()
		);

		// TODO convertXmlToApiBookDto에서 item 항목이 없다면(검색 결과가 없다면) 추가 정보 넘겨주기
	}

	@Test
	@DisplayName("서적 저장")
	void saveBooks() {
		List<RenderBookDto> books = new ArrayList<>();
		RenderBookDto book = getRenderBookDto();
		books.add(book);
		when(bookService.saveBook(any())).thenReturn(null);

		adminService.saveBooks(books);

		verify(bookService).saveBook(bookEntityArgumentCaptor.capture());
		verify(bookService, times(books.size())).saveBook(any());
		BookEntity bookEntity = bookEntityArgumentCaptor.getValue();
		assertAll(
				() -> assertThat(bookEntity).isNotNull(),
				() -> assertThat(bookEntity.getIsbn()).isEqualTo(book.getIsbn()),
				() -> assertThat(bookEntity.getTitle()).isEqualTo(book.getTitle()),
				() -> assertThat(bookEntity.getAuthor()).isEqualTo(book.getAuthor()),
				() -> assertThat(bookEntity.getPublisher()).isEqualTo(book.getPublisher()),
				() -> assertThat(bookEntity.getOriginPrice()).isEqualTo(book.getOriginPrice()),
				() -> assertThat(bookEntity.getPublishDate()).isEqualTo(book.getPublishDate()),
				() -> assertThat(bookEntity.getDescription()).isEqualTo(book.getDescription()),
				() -> assertThat(bookEntity.getOriginImageUrl()).isEqualTo(book.getImageUrl())
		);
	}

	@Test
	@DisplayName("기존 서적 전체 가져오기 성공")
	void findAllOk() {
		List<BookEntity> books = new ArrayList<>();
		BookEntity book = getBookEntity();
		books.add(book);
		when(bookService.findAll()).thenReturn(books);

		List<RenderBookDto> renderBookDtos = adminService.findAll();

		assertAll(
				() -> assertThat(renderBookDtos).isNotNull(),
				() -> assertThat(renderBookDtos.size()).isEqualTo(books.size()),
				() -> assertThat(renderBookDtos.get(0).getIsbn()).isEqualTo(book.getIsbn()),
				() -> assertThat(renderBookDtos.get(0).getTitle()).isEqualTo(book.getTitle()),
				() -> assertThat(renderBookDtos.get(0).getAuthor()).isEqualTo(book.getAuthor()),
				() -> assertThat(renderBookDtos.get(0).getPublisher()).isEqualTo(book.getPublisher()),
				() -> assertThat(renderBookDtos.get(0).getOriginPrice()).isEqualTo(book.getOriginPrice()),
				() -> assertThat(renderBookDtos.get(0).getPublishDate()).isEqualTo(book.getPublishDate()),
				() -> assertThat(renderBookDtos.get(0).getDescription()).isEqualTo(book.getDescription()),
				() -> assertThat(renderBookDtos.get(0).getImageUrl()).isEqualTo(book.getS3ImageUrl())
		);
	}

	@Test
	@DisplayName("기존 서적 전체 가져오기, 서적이 없을 경우")
	void findAllEmpty() {
		List<BookEntity> books = new ArrayList<>();
		when(bookService.findAll()).thenReturn(books);

		List<RenderBookDto> renderBookDtos = adminService.findAll();

		assertAll(
				() -> assertThat(renderBookDtos).isNotNull(),
				() -> assertThat(renderBookDtos.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 기존 서적 검색 성공")
	void findByIsbnOk() {
		BookEntity book = getBookEntity();
		when(bookService.findByIsbn(book.getIsbn())).thenReturn(book);

		RenderBookDto renderBookDto = adminService.findByIsbn(book.getIsbn());

		assertAll(
				() -> assertThat(renderBookDto).isNotNull(),
				() -> assertThat(renderBookDto.getIsbn()).isEqualTo(book.getIsbn()),
				() -> assertThat(renderBookDto.getTitle()).isEqualTo(book.getTitle()),
				() -> assertThat(renderBookDto.getAuthor()).isEqualTo(book.getAuthor()),
				() -> assertThat(renderBookDto.getPublisher()).isEqualTo(book.getPublisher()),
				() -> assertThat(renderBookDto.getOriginPrice()).isEqualTo(book.getOriginPrice()),
				() -> assertThat(renderBookDto.getPublishDate()).isEqualTo(book.getPublishDate()),
				() -> assertThat(renderBookDto.getDescription()).isEqualTo(book.getDescription()),
				() -> assertThat(renderBookDto.getImageUrl()).isEqualTo(book.getS3ImageUrl())
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 기존 서적 검색, 해당 Isbn을 가진 서적이 없을 경우")
	void findByIsbnNotFound() {
		long isbn = 135L;
		when(bookService.findByIsbn(isbn)).thenReturn(null);

		RenderBookDto renderBookDto = adminService.findByIsbn(isbn);

		assertAll(
				() -> assertThat(renderBookDto).isNull()
		);
	}

	@Test
	@DisplayName("서적 정보 갱신")
	void updateBook() {
		RenderBookDto book = getRenderBookDto();
		doNothing().when(bookService).updateBook(any());

		adminService.updateBook(book);

		verify(bookService).updateBook(bookEntityArgumentCaptor.capture());
		verify(bookService, times(1)).updateBook(any());
		BookEntity bookEntity = bookEntityArgumentCaptor.getValue();
		assertAll(
				() -> assertThat(bookEntity).isNotNull(),
				() -> assertThat(bookEntity.getIsbn()).isEqualTo(book.getIsbn()),
				() -> assertThat(bookEntity.getTitle()).isEqualTo(book.getTitle()),
				() -> assertThat(bookEntity.getAuthor()).isEqualTo(book.getAuthor()),
				() -> assertThat(bookEntity.getPublisher()).isEqualTo(book.getPublisher()),
				() -> assertThat(bookEntity.getOriginPrice()).isEqualTo(book.getOriginPrice()),
				() -> assertThat(bookEntity.getPublishDate()).isEqualTo(book.getPublishDate()),
				() -> assertThat(bookEntity.getDescription()).isEqualTo(book.getDescription()),
				() -> assertThat(bookEntity.getOriginImageUrl()).isEqualTo(book.getImageUrl())
		);
	}

	@Test
	@DisplayName("ApiBookDto를 RenderBookDto로 변환 성공")
	void convertApiBookDtoToRenderBookDtoExist() {
		List<ApiBookDto> books = new ArrayList<>();
		ApiBookDto book = getApiBookDto();
		books.add(book);

		List<RenderBookDto> renderBookDtos = adminService.convertApiBookDtoToRenderBookDto(books);

		assertAll(
				() -> assertThat(renderBookDtos).isNotNull(),
				() -> assertThat(renderBookDtos.size()).isEqualTo(1)
		);
	}

	@Test
	@DisplayName("ApiBookDto를 RenderBookDto로 변환, ApiBookDto가 없는 경우")
	void convertApiBookDtoToRenderBookDtoEmpty() {
		List<ApiBookDto> books = new ArrayList<>();

		List<RenderBookDto> renderBookDtos = adminService.convertApiBookDtoToRenderBookDto(books);

		assertAll(
				() -> assertThat(renderBookDtos).isNotNull(),
				() -> assertThat(renderBookDtos.isEmpty()).isTrue()
		);
	}
}
