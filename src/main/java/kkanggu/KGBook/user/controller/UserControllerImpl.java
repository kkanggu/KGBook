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
		Long id = userService.saveUser(user);
		return id;
	}

	@Override
	public List<UserEntity> findAll() {
		List<UserEntity> users = userService.findAll();
		return users;
	}

	@Override
	public UserEntity findById(Long id) {
		UserEntity user = userService.findById(id);
		return user;
	}
}
