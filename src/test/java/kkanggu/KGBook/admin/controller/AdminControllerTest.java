package kkanggu.KGBook.admin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import kkanggu.KGBook.admin.dto.ApiBookDto;
import kkanggu.KGBook.admin.service.AdminService;
import kkanggu.KGBook.book.dto.RenderBookDto;

@WebMvcTest(AdminController.class)
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
	private final MockMvc mockMvc;

	@Autowired
	public AdminControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@MockBean
	private AdminService adminService;

	@Captor
	private ArgumentCaptor<List<RenderBookDto>> renderBookDtoArgumentCaptor;

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

	@Test
	@DisplayName("GET /admin, 최초 admin 페이지")
	void main() throws Exception {
		mockMvc.perform(get("/admin"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/main"));
	}

	@Test
	@DisplayName("GET /admin/books, 서적 조회")
	void booksOk() throws Exception {
		List<RenderBookDto> books = new ArrayList<>();
		books.add(getRenderBookDto());
		when(adminService.findAll()).thenReturn(books);

		mockMvc.perform(get("/admin/books"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/books"))
				.andExpect(model().attributeExists("books"))
				.andExpect(model().attribute("books", books));
	}

	@Test
	@DisplayName("GET /admin/books, 서적이 없을 경우")
	void booksEmpty() throws Exception {
		List<RenderBookDto> books = new ArrayList<>();
		when(adminService.findAll()).thenReturn(books);

		mockMvc.perform(get("/admin/books"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/books"))
				.andExpect(model().attributeExists("books"))
				.andExpect(model().attribute("books", books));
	}

	@Test
	@DisplayName("GET /admin/book/{isbn}, 서적 상세 조회 성공")
	void getBookOk() throws Exception {
		RenderBookDto book = getRenderBookDto();
		when(adminService.findByIsbn(book.getIsbn())).thenReturn(book);

		mockMvc.perform(get("/admin/book/{isbn}", book.getIsbn()))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/book"))
				.andExpect(model().attributeExists("book"))
				.andExpect(model().attribute("book", book))
				.andExpect(model().attributeExists("isEdit"))
				.andExpect(model().attribute("isEdit", false));
	}

	@Test
	@DisplayName("GET /admin/book/{isbn}, 서적 상세 조회 실패")
	void getBookFail() throws Exception {
		long isbn = 135L;
		when(adminService.findByIsbn(isbn)).thenReturn(null);

		mockMvc.perform(get("/admin/book/{isbn}", isbn))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/books"));
	}

	@Test
	@DisplayName("POST /admin/book/{isbn}, 서적 업데이트")
	void updateBook() throws Exception {
		RenderBookDto book = getRenderBookDto();
		doNothing().when(adminService).updateBook(book);

		mockMvc.perform(post("/admin/book/{isbn}", book.getIsbn()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/book/" + book.getIsbn()));
	}

	@Test
	@DisplayName("GET /admin/book/{isbn}/edit, 서적 수정 페이지")
	void editBookOk() throws Exception {
		RenderBookDto book = getRenderBookDto();
		when(adminService.findByIsbn(book.getIsbn())).thenReturn(book);

		mockMvc.perform(get("/admin/book/{isbn}/edit", book.getIsbn()))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/book"))
				.andExpect(model().attributeExists("book"))
				.andExpect(model().attribute("book", book))
				.andExpect(model().attributeExists("isEdit"))
				.andExpect(model().attribute("isEdit", true));
	}

	@Test
	@DisplayName("GET /admin/book/{isbn}/edit, 서적이 없을 경우")
	void editBookNotFound() throws Exception {
		long isbn = 135L;
		when(adminService.findByIsbn(isbn)).thenReturn(null);

		mockMvc.perform(get("/admin/book/{isbn}/edit", isbn))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/books"));
	}

	@Test
	@DisplayName("GET /admin/book/new, 신규 서적 검색 페이지")
	void searchNewBook() throws Exception {
		mockMvc.perform(get("/admin/book/new"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/searchNewBook"));
	}

	@Test
	@DisplayName("POST /admin/book/new, 신규 서적 검색 성공")
	void fetchNewBookOk() throws Exception {
		List<ApiBookDto> apiBookDtos = new ArrayList<>();
		List<RenderBookDto> renderBookDtos = new ArrayList<>();
		renderBookDtos.add(getRenderBookDto());
		ResponseEntity<String> responseEntity = ResponseEntity.ok("Mocking Response");
		String keyword = "Test";
		String searchRecent = "asdf";
		boolean isSearchRecent = "on".equals(searchRecent);
		when(adminService.fetchBookFromNaverApi(keyword, isSearchRecent)).thenReturn(responseEntity);
		when(adminService.convertXmlToApiBookDto(responseEntity.getBody())).thenReturn(apiBookDtos);
		when(adminService.convertApiBookDtoToRenderBookDto(apiBookDtos)).thenReturn(renderBookDtos);

		mockMvc.perform(post("/admin/book/new")
						.param("keyword", keyword)
						.param("searchRecent", searchRecent))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/book/new/list"))
				.andExpect(flash().attributeExists("books"))
				.andExpect(flash().attribute("books", renderBookDtos));
	}

	@Test
	@DisplayName("POST /admin/book/new, 신규 서적 검색 실패")
	void fetchNewBookFail() throws Exception {
		ResponseEntity<String> responseEntity = ResponseEntity.badRequest().build();
		String keyword = "Test";
		String searchRecent = "asdf";
		boolean isSearchRecent = "on".equals(searchRecent);
		when(adminService.fetchBookFromNaverApi(keyword, isSearchRecent)).thenReturn(responseEntity);

		mockMvc.perform(post("/admin/book/new")
						.param("keyword", keyword)
						.param("searchRecent", searchRecent))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/book/new"));
	}

	@Test
	@DisplayName("GET /admin/book/new/list, 검색한 신규 서적 전체 조회")
	void listNewBooks() throws Exception {
		List<RenderBookDto> books = new ArrayList<>();
		books.add(getRenderBookDto());

		MvcResult mvcResult = mockMvc.perform(get("/admin/book/new/list")
						.flashAttr("books", books))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/newBooks"))
				.andExpect(model().attributeExists("books"))
				.andExpect(model().attribute("books", books))
				.andReturn();

		List<RenderBookDto> sessionBooks = (List<RenderBookDto>) mvcResult.getRequest().getSession().getAttribute("books");
		assertThat(sessionBooks).isNotNull();
		assertThat(sessionBooks).isEqualTo(books);
	}

	@Test
	@DisplayName("GET /admin/book/new/{isbn}}, 검색한 신규 서적 개별 조회 성공")
	void getNewBookOk() throws Exception {
		List<RenderBookDto> books = new ArrayList<>();
		books.add(getRenderBookDto());
		books.add(getRenderBookDto());
		books.get(1).setIsbn(1L);
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute("books", books);

		mockMvc.perform(get("/admin/book/new/{isbn}", books.get(0).getIsbn())
						.session(mockHttpSession))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/editNewBook"))
				.andExpect(model().attributeExists("book"))
				.andExpect(model().attribute("book", books.get(0)));
	}

	@Test
	@DisplayName("GET /admin/book/new/{isbn}}, 해당 서적이 없을 경우")
	void getNewBookNotFound() throws Exception {
		long isbn = 135L;
		List<RenderBookDto> books = new ArrayList<>();
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute("books", books);

		mockMvc.perform(get("/admin/book/new/{isbn}", isbn)
						.session(mockHttpSession))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/book/new/list"));
	}

	@Test
	@DisplayName("POST /admin/book/new/{isbn}, 검색한 신규 서적 수정 성공")
	void editNewBookOk() throws Exception {
		List<RenderBookDto> books = new ArrayList<>();
		books.add(getRenderBookDto());
		RenderBookDto renderBookDto = books.get(0);
		renderBookDto.setTitle("changeTitle");
		renderBookDto.setAuthor("changeAuthor");
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute("books", books);

		MvcResult mvcResult = mockMvc.perform(post("/admin/book/new/{isbn}", books.get(0).getIsbn())
						.flashAttr("renderBookDto", renderBookDto)
						.session(mockHttpSession))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/book/new/list"))
				.andExpect(flash().attributeExists("books"))
				.andReturn();
		List<RenderBookDto> flashBooks = (List<RenderBookDto>) mvcResult.getFlashMap().get("books");
		assertAll(
				() -> assertThat(flashBooks.get(0).getTitle()).isEqualTo(renderBookDto.getTitle()),
				() -> assertThat(flashBooks.get(0).getAuthor()).isEqualTo(renderBookDto.getAuthor())
		);
	}

	@Test
	@DisplayName("POST /admin/book/new/save, 검색한 신규 서적 저장")
	void saveNewBooks() throws Exception {
		List<RenderBookDto> books = new ArrayList<>();
		List<RenderBookDto> renderBookDtos = new ArrayList<>();
		books.add(getRenderBookDto());
		books.add(getRenderBookDto());
		books.get(1).setIsbn(1L);
		renderBookDtos.add(books.get(0));
		List<Boolean> selectedBooks = new ArrayList<>();
		selectedBooks.add(true);
		selectedBooks.add(false);
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute("books", books);
		doNothing().when(adminService).saveBooks(renderBookDtos);

		mockMvc.perform(post("/admin/book/new/save")
						.flashAttr("selectedBooks", selectedBooks)
						.session(mockHttpSession))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/books"));

		verify(adminService).saveBooks(renderBookDtoArgumentCaptor.capture());
		List<RenderBookDto> argumentCaptorValue = renderBookDtoArgumentCaptor.getValue();
		assertThat(argumentCaptorValue.size()).isEqualTo(1);
		assertThat(argumentCaptorValue.get(0)).isEqualTo(books.get(0));
	}

	@Test
	void users() throws Exception {
		mockMvc.perform(get("/admin/users"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/users"));
	}
}
