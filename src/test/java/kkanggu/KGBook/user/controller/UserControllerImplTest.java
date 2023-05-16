package kkanggu.KGBook.user.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.repository.BookOwnerOrderRepository;
import kkanggu.KGBook.book.repository.BookRepository;
import kkanggu.KGBook.common.aws.ImageController;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class UserControllerImplTest {
	private final JdbcTemplate jdbcTemplate;
	private final UserController userController;
	private final UserRepository userRepository;
	private final BookRepository bookRepository;
	private final BookOwnerOrderRepository bookOwnerOrderRepository;
	private final ImageController imageController;

	@Autowired
	public UserControllerImplTest(JdbcTemplate jdbcTemplate,
								  UserController userController,
								  UserRepository userRepository,
								  BookRepository bookRepository,
								  BookOwnerOrderRepository bookOwnerOrderRepository,
								  ImageController imageController) {
		this.userController = userController;
		this.jdbcTemplate = jdbcTemplate;
		this.userRepository = userRepository;
		this.bookRepository = bookRepository;
		this.bookOwnerOrderRepository = bookOwnerOrderRepository;
		this.imageController = imageController;
	}

	void insertUsersBeforeTest() {
		for (int i = 0; i < 4; ++i) {
			UserEntity user = new UserEntity(null, "user" + i, "password" + i, null, null, null, LocalDate.now());
			userController.saveUser(user);
		}
	}

	@BeforeEach
	void setup() {
		jdbcTemplate.update("DELETE FROM USER");
	}

	@Test
	@DisplayName("유저 정보를 받아 저장")
	void saveUserTest() {
		// given
		UserEntity user = new UserEntity(null, "save", "ssave", null, null, null, LocalDate.now());

		// when
		Long id = userController.saveUser(user);

		// then
		UserEntity findUser = userRepository.findById(id);
		assertThat(findUser.getId()).isEqualTo(id);
		assertThat(findUser.getUsername()).isEqualTo(user.getUsername());
		assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
		assertThat(findUser.getCreateDate()).isEqualTo(user.getCreateDate());
	}

	@Test
	@DisplayName("유저 전체 가져오기")
	void findAllTest() {
		// given
		insertUsersBeforeTest();

		// when
		List<UserEntity> users = userController.findAll();

		// then
		assertThat(users.size()).isEqualTo(4);
	}

	@Test
	@DisplayName("id를 이용하여 유저 가져오기")
	void findByIdTest() {
		// given
		insertUsersBeforeTest();
		UserEntity user = new UserEntity(null, "user", "passFindById", null, null, null, LocalDate.now());
		Long id = userRepository.saveUser(user);

		// when
		UserEntity findUser = userController.findById(id);

		// then
		assertThat(findUser.getId()).isEqualTo(id);
		assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
	}

	@Test
	@DisplayName("isbn을 이용하여 서적을 소유중인 유저 가져오기")
	void findUsersHaveBookTest() {
		// given
		BookEntity book = new BookEntity(1357924680130L, "book", "author", "publisher",
				LocalDate.now(), LocalDate.now(),"description",
				"https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg", null);
		Long isbn = bookRepository.saveBook(book);

		UserEntity user = new UserEntity(null, "username", "pass", null, null, null, LocalDate.now());
		Long id1 = userRepository.saveUser(user);
		Long id2 = userRepository.saveUser(user);

		bookOwnerOrderRepository.saveBookUserOwn(1357924680130L, id1);
		bookOwnerOrderRepository.saveBookUserOwn(1357924680130L, id2);

		// when
		List<UserEntity> users = userController.findUsersHaveBook(1357924680130L);

		// then
		assertThat(users).isNotNull();
		assertThat(users.size()).isEqualTo(2);
		assertThat(users.get(0).getId()).isEqualTo(id1);
		assertThat(users.get(1).getId()).isEqualTo(id2);

		BookEntity savedBook = bookRepository.findByIsbn(isbn);
		boolean isDeleted = imageController.deleteImage(savedBook.getS3ImageUrl());
		assertThat(isDeleted).isEqualTo(true);
	}
}
