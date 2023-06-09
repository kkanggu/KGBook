package kkanggu.KGBook.admin.controller;

import java.beans.PropertyEditorSupport;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import kkanggu.KGBook.admin.dto.ApiBookDto;
import kkanggu.KGBook.admin.service.AdminService;
import kkanggu.KGBook.book.dto.RenderBookDto;
import lombok.extern.slf4j.Slf4j;

// TODO : Validation

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {
	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@InitBinder("renderBookDto")
	public void initBinder(WebDataBinder webDataBinder) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		webDataBinder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				setValue(LocalDate.parse(text, formatter));
			}
		});
	}

	@GetMapping("")
	public String main() {
		return "admin/main";
	}

	@GetMapping("/books")
	public String books(Model model) {
		List<RenderBookDto> books = adminService.findAll();

		model.addAttribute("books", books);

		return "admin/books";
	}

	@GetMapping("/book/{isbn}")
	public String getBook(@PathVariable long isbn, Model model) {
		RenderBookDto book = adminService.findByIsbn(isbn);

		if (null == book) {
			log.warn("Book with isbn {} not exist.", isbn);
			return "admin/books";
		}

		model.addAttribute("book", book);
		model.addAttribute("isEdit", false);

		return "admin/book";
	}

	@PostMapping("/book/{isbn}")
	public String updateBook(RenderBookDto renderBookDto) {
		adminService.updateBook(renderBookDto);

		return "redirect:/admin/book/" + renderBookDto.getIsbn();
	}

	@GetMapping("/book/{isbn}/edit")
	public String editBook(@PathVariable long isbn, Model model) {
		RenderBookDto book = adminService.findByIsbn(isbn);

		if (null == book) {
			log.warn("No book found with isbn {}", isbn);
			return "redirect:/admin/books";
		}

		model.addAttribute("book", book);
		model.addAttribute("isEdit", true);

		return "admin/book";
	}

	@GetMapping("/book/new")
	public String searchNewBook() {
		return "admin/searchNewBook";
	}

	@PostMapping("/book/new")
	public String fetchNewBook(@ModelAttribute("keyword") String keyword,
							   @ModelAttribute("searchRecent") String searchRecent,
							   RedirectAttributes redirectAttributes) {
		boolean isSearchRecent = "on".equals(searchRecent);
		String decodeKeyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8);
		ResponseEntity<String> responseEntity = adminService.fetchBookFromNaverApi(decodeKeyword, isSearchRecent);

		if (HttpStatus.OK != responseEntity.getStatusCode()) {
			log.warn("API fetch failed. headers : {}, body : {}", responseEntity.getHeaders(), responseEntity.getBody());
			return "redirect:/admin/book/new";
		}

		List<ApiBookDto> apiBookDtos = adminService.convertXmlToApiBookDto(responseEntity.getBody());
		List<RenderBookDto> renderBookDtos = adminService.convertApiBookDtoToRenderBookDto(apiBookDtos);

		redirectAttributes.addFlashAttribute("books", renderBookDtos);

		return "redirect:/admin/book/new/list";
	}

	@GetMapping("/book/new/list")
	public String listNewBooks(@ModelAttribute("books") List<RenderBookDto> books,
							   HttpSession httpSession) {
		httpSession.setAttribute("books", books);

		return "admin/newBooks";
	}

	@GetMapping("/book/new/{isbn}")
	public String getNewBook(@PathVariable long isbn,
							 HttpSession httpSession,
							 RedirectAttributes redirectAttributes,
							 Model model) {
		List<RenderBookDto> books = (List<RenderBookDto>) httpSession.getAttribute("books");
		RenderBookDto book = books.stream()
				.filter(renderBookDto -> renderBookDto.getIsbn() == isbn)
				.findAny()
				.orElse(null);

		if (null == book) {
			redirectAttributes.addFlashAttribute("books", books);
			log.warn("No new book found with isbn {}", isbn);
			return "redirect:/admin/book/new/list";
		}

		model.addAttribute("book", book);

		return "admin/editNewBook";
	}

	@PostMapping("/book/new/{isbn}")
	public String editNewBook(@ModelAttribute RenderBookDto renderBookDto,
							  HttpSession httpSession,
							  RedirectAttributes redirectAttributes) {
		List<RenderBookDto> books = (List<RenderBookDto>) httpSession.getAttribute("books");

		books = books.stream()
				.map(book -> {
					if (book.getIsbn().equals(renderBookDto.getIsbn())) {
						return RenderBookDto.builder()
								.isbn(book.getIsbn())
								.title(renderBookDto.getTitle())
								.author(renderBookDto.getAuthor())
								.publisher(renderBookDto.getPublisher())
								.originPrice(book.getOriginPrice())
								.publishDate(renderBookDto.getPublishDate())
								.description(renderBookDto.getDescription())
								.imageUrl(book.getImageUrl())
								.build();
					}
					return book;
				})
				.toList();

		redirectAttributes.addFlashAttribute("books", books);

		return "redirect:/admin/book/new/list";
	}

	@PostMapping("/book/new/save")
	public String saveNewBooks(HttpSession httpSession,
							   @ModelAttribute("selectedBooks") List<Boolean> selectedBooks) {
		List<RenderBookDto> books = (List<RenderBookDto>) httpSession.getAttribute("books");

		List<RenderBookDto> renderBookDtos = IntStream.range(0, books.size())
				.filter(selectedBooks::get)
				.mapToObj(books::get)
				.toList();

		adminService.saveBooks(renderBookDtos);

		return "redirect:/admin/books";
	}

	@GetMapping("/users")
	public String users() {
		return "admin/users";
	}
}
