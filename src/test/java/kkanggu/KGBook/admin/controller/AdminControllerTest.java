package kkanggu.KGBook.admin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import kkanggu.KGBook.admin.service.AdminService;

@WebMvcTest(AdminController.class)
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
	void users() throws Exception {
		mockMvc.perform(get("/admin/users"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/users"));
	}
}
