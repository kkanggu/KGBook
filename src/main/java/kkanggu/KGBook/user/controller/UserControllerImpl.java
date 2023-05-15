package kkanggu.KGBook.user.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.service.UserService;

@Controller
public class UserControllerImpl implements UserController {
	private final UserService userService;

	public UserControllerImpl(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Long saveUser(UserEntity user) {
		return userService.saveUser(user);
	}

	@Override
	public List<UserEntity> findAll() {
		return userService.findAll();
	}

	@Override
	public UserEntity findById(Long id) {
		return userService.findById(id);
	}

	@Override
	public List<UserEntity> findUsersHaveBook(long isbn) {
		List<Long> userIds = userService.findUserIdByIsbn(isbn);

		return userService.findById(userIds);
	}
}
