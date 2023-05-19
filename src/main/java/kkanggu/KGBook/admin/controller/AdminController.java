package kkanggu.KGBook.admin.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import kkanggu.KGBook.admin.service.AdminService;
import kkanggu.KGBook.book.dto.RenderBookDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {
	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("")
	public String main() {
		return "admin/main";
	}

	@GetMapping("/books")
	public String books(Model model) throws IOException {
		List<RenderBookDto> books = adminService.findAll();

		model.addAttribute("books", books);

		return "admin/books";
	}

	@GetMapping("/users")
	public String users() {
		return "admin/users";
	}
}
