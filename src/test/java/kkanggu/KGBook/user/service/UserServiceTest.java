package kkanggu.KGBook.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
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
class UserServiceTest {
	private final JdbcTemplate jdbcTemplate;
	private final UserService userService;
	private final UserRepository userRepository;
	private final BookRepository bookRepository;
	private final BookOwnerOrderRepository bookOwnerOrderRepository;
	private final ImageController imageController;

	@Autowired
	public UserServiceTest(JdbcTemplate jdbcTemplate,
						   UserService userService,
						   UserRepository userRepository,
						   BookRepository bookRepository,
						   BookOwnerOrderRepository bookOwnerOrderRepository,
						   ImageController imageController) {
		this.userService = userService;
		this.jdbcTemplate = jdbcTemplate;
		this.userRepository = userRepository;
		this.bookRepository = bookRepository;
		this.bookOwnerOrderRepository = bookOwnerOrderRepository;
		this.imageController = imageController;
	}

	void insertUsersBeforeTest() {
		for (int i = 0; i < 4; ++i) {
			UserEntity user = new UserEntity(null, "user" + i, "password" + i, null, null, null, LocalDate.now());
			userService.saveUser(user);
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
		Long id = userService.saveUser(user);

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
		List<UserEntity> users = userService.findAll();

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
		UserEntity findUser = userService.findById(id);

		// then
		assertThat(findUser.getId()).isEqualTo(id);
		assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
	}

	@Test
	@DisplayName("특정 서적을 가지고 있는 유저들의 id를 가져옴")
	void findUserIdByIsbnTest() {
		// given
		UserEntity user = new UserEntity(null, "username", "pass", null, null, null, LocalDate.now());
		Long userId = userRepository.saveUser(user);

		BookEntity book = new BookEntity(1357924680130L, "book", "author", "publisher",
				LocalDate.now(), LocalDate.now(),"description",
				"https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg", null);
		bookRepository.saveBook(book);

		bookOwnerOrderRepository.saveBookUserOwn(1357924680130L, userId);

		// when
		List<Long> userIds = userService.findUserIdByIsbn(book.getIsbn());

		// then
		assertThat(userIds.size()).isEqualTo(1);
		assertThat(userIds.get(0)).isEqualTo(userId);

		BookEntity findBook = bookRepository.findByIsbn(book.getIsbn());
		boolean isDeleted = imageController.deleteImage(findBook.getS3ImageUrl());
		assertThat(isDeleted).isEqualTo(true);
	}

	@Test
	@DisplayName("id들을 이용하여 유저 가져오기")
	void findByIdsTest() {
		// given
		List<Long> ids = new ArrayList<>();
		for (int i = 0; i < 4; ++i) {
			UserEntity user = new UserEntity(null, "user", "password", null, null, null, LocalDate.now());
			Long id = userRepository.saveUser(user);
			ids.add(id);
		}

		// when
		List<UserEntity> users = userService.findById(ids);

		// then
		assertThat(users.size()).isEqualTo(4);
		for (int i = 0; i < 4; ++i) {
			assertThat(users.get(i).getId()).isEqualTo(ids.get(i));
		}
	}
}
