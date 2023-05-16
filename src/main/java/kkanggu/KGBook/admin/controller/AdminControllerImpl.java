package kkanggu.KGBook.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import kkanggu.KGBook.admin.dto.ApiBookDto;
import kkanggu.KGBook.admin.service.AdminService;
import kkanggu.KGBook.book.entity.BookEntity;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminControllerImpl implements AdminController {
	private final AdminService adminService;

	public AdminControllerImpl(AdminService adminService) {
		this.adminService = adminService;
	}

	@Override
	public List<BookEntity> searchBooks(String keyword, boolean searchRecent) {
		ResponseEntity<String> naverApiResponseEntity = adminService.fetchBookFromNaverApi(keyword, searchRecent);

		if (naverApiResponseEntity.getStatusCode().is2xxSuccessful()) {
			List<ApiBookDto> apiBookDtos = adminService.convertJsonToApiBookDto(naverApiResponseEntity.getBody());
			return adminService.convertApiBookDtoToBookEntity(apiBookDtos);
		} else {
			log.warn("Naver Book Search Api Failed");
		}

		return null;
	}

	@Override
	public void saveBooks(List<BookEntity> books) {
		adminService.saveBooks(books);
	}
}
