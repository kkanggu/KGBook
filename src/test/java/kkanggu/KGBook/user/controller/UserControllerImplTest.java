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
import kkanggu.KGBook.user.entity.UserEntity;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class UserControllerImplTest {
	private final UserController userController;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public UserControllerImplTest(UserController userController,
								  JdbcTemplate jdbcTemplate) {
		this.userController = userController;
		this.jdbcTemplate = jdbcTemplate;
	}

	void insertUsersBeforeTest() {
		for (int i = 0; i < 4; ++i) {
			UserEntity user = new UserEntity((long) i, "user" + i, "password" + i, null, null, null, LocalDate.now());
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
		UserEntity user = new UserEntity(1L, "save", "ssave", null, null, null, LocalDate.now());

		// when
		Long id = userController.saveUser(user);

		// then
		UserEntity findUser = userController.findById(id);
		assertThat(findUser.getId()).isEqualTo(user.getId());
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
		UserEntity user = new UserEntity(5L, "user", "passFindById", null, null, null, LocalDate.now());
		Long id = userController.saveUser(user);

		// when
		UserEntity findUser = userController.findById(id);

		// then
		assertThat(findUser.getId()).isEqualTo(id);
		assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
	}

}
