package kkanggu.KGBook.user.controller;

import java.util.List;

import kkanggu.KGBook.user.entity.UserEntity;

public interface UserController {
	Long saveUser(UserEntity user);

	List<UserEntity> findAll();

	UserEntity findById(Long id);

	List<UserEntity> findUsersHaveBook(long isbn);
}
