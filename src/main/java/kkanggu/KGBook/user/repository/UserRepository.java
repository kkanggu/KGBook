package kkanggu.KGBook.user.repository;

import java.util.List;

import kkanggu.KGBook.user.entity.UserEntity;

public interface UserRepository {
	Long getMaxId();

	Long saveUser(UserEntity user);

	List<UserEntity> findAll();

	UserEntity findById(Long id);
}
