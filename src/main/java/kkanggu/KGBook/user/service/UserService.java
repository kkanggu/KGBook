package kkanggu.KGBook.user.service;

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

	public List<UserEntity> findUsersHaveBook(long isbn) {
		List<Long> userIds = bookOwnerOrderRepository.findUserIdByIsbn(isbn);

		return userIds.stream()
				.map(userRepository::findById)
				.toList();
	}
}
