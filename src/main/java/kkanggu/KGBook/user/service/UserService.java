package kkanggu.KGBook.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import kkanggu.KGBook.book.repository.BookOwnerOrderRepository;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final BookOwnerOrderRepository bookOwnerOrderRepository;

	public UserService(UserRepository userRepository,
					   BookOwnerOrderRepository bookOwnerOrderRepository) {
		this.userRepository = userRepository;
		this.bookOwnerOrderRepository = bookOwnerOrderRepository;
	}

	public Long saveUser(UserEntity user) {
		return userRepository.saveUser(user);
	}

	public List<UserEntity> findAll() {
		return userRepository.findAll();
	}

	public UserEntity findById(Long id) {
		return userRepository.findById(id);
	}

	public List<Long> findUserIdByIsbn(long isbn) {
		return bookOwnerOrderRepository.findUserIdByIsbn(isbn);
	}

	/**
	 * If over 10K user own same book, then 10K query will execute
	 * This can lower performance
	 */
	public List<UserEntity> findById(List<Long> userIds) {
		List<UserEntity> users = new ArrayList<>();
		for (Long userId : userIds) {
			UserEntity user = userRepository.findById(userId);
			users.add(user);
		}

		return users;
	}
}
