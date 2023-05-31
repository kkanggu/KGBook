package kkanggu.KGBook.user.service;

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
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class UserServiceTest {
	private final JdbcTemplate jdbcTemplate;
	private final UserService userService;
	private final UserRepository userRepository;

	@Autowired
	public UserServiceTest(JdbcTemplate jdbcTemplate,
						   UserService userService,
						   UserRepository userRepository) {
		this.userService = userService;
		this.jdbcTemplate = jdbcTemplate;
		this.userRepository = userRepository;
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
}
