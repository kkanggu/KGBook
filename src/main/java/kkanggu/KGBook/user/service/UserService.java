package kkanggu.KGBook.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Long saveUser(UserEntity user) {
		Long id = userRepository.saveUser(user);
		return id;
	}

	public List<UserEntity> findAll() {
		List<UserEntity> users = userRepository.findAll();
		return users;
	}

	public UserEntity findById(Long id) {
		UserEntity user = userRepository.findById(id);
		return user;
	}
}
