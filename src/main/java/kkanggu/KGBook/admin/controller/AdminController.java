package kkanggu.KGBook.admin.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import kkanggu.KGBook.admin.service.AdminService;
import kkanggu.KGBook.book.dto.RenderBookDto;
import lombok.extern.slf4j.Slf4j;

// TODO : Validation
// TODO : With updateBook, need to pass BookEntity not RenderBookDto

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

		model.addAttribute("book", book);
		model.addAttribute("isEdit", true);

		return "admin/book";
	}

	@GetMapping("/users")
	public String users() {
		return "admin/users";
	}
}
