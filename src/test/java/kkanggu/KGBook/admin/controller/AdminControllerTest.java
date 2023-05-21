package kkanggu.KGBook.admin.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
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

	@Test
	void main() throws Exception {
		mockMvc.perform(get("/admin"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/main"));
	}

	@Test
	void books() throws Exception {
		mockMvc.perform(get("/admin/books"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/books"));
	}

	@Test
	@DisplayName("서적이 존재할 때 서적 상세 조회")
	void bookPass() throws Exception {
		RenderBookDto renderBookDto = new RenderBookDto();
		renderBookDto.setIsbn(1234L);

		when(adminService.findByIsbn(renderBookDto.getIsbn())).thenReturn(renderBookDto);

		mockMvc.perform(get("/admin/book/{isbn}", renderBookDto.getIsbn()))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/book"));
	}

	@Test
	@DisplayName("서적이 존재하지 않을 때 서적 목록으로 다시 돌아감")
	void bookFail() throws Exception {
		long isbn = 0L;

		when(adminService.findByIsbn(isbn)).thenReturn(null);

		mockMvc.perform(get("/admin/book/{isbn}", isbn))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/books"));
	}


	@Test
	void users() throws Exception {
		mockMvc.perform(get("/admin/users"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/users"));
	}
}
