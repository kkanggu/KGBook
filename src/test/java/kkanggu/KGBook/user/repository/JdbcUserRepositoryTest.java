package kkanggu.KGBook.user.repository;

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
class JdbcUserRepositoryTest {
	private final JdbcTemplate jdbcTemplate;
	private final JdbcUserRepository jdbcUserRepository;

	@Autowired
	public JdbcUserRepositoryTest(JdbcTemplate jdbcTemplate,
								  JdbcUserRepository jdbcUserRepository) {
		this.jdbcTemplate = jdbcTemplate;
		this.jdbcUserRepository = jdbcUserRepository;
	}

	void insertUsersBeforeTest() {
		for (int i = 0; i < 4; ++i) {
			UserEntity user = new UserEntity((long) i, "user" + i, "password" + i, null, null, null, LocalDate.now());
			jdbcUserRepository.saveUser(user);
		}
	}

	@BeforeEach
	void init() {
		jdbcTemplate.update("DELETE FROM USER");
	}

	@Test
	@DisplayName("유저들 중 최대 id 가져오기")
	void getMaxIdTest() {
		// given
		UserEntity user = new UserEntity(1L, "user", "temp", null, null, null, LocalDate.now());

		// when
		Long id = jdbcUserRepository.saveUser(user);

		// then
		Long findId = jdbcUserRepository.getMaxId();

		assertThat(id).isNotNull();
		assertThat(findId).isEqualTo(id);
	}

	@Test
	@DisplayName("유저 저장")
	void saveUserTest() {
		// given
		UserEntity user = new UserEntity(1L, "user", "passSaveUser", null, null, null, LocalDate.now());
		Long id = jdbcUserRepository.saveUser(user);

		// when
		UserEntity findUser = jdbcUserRepository.findById(id);

		// then
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
		List<UserEntity> users = jdbcUserRepository.findAll();

		// then
		assertThat(users.size()).isEqualTo(4);
	}

	@Test
	@DisplayName("id를 이용하여 유저 가져오기")
	void findByIdTest() {
		// given
		insertUsersBeforeTest();
		UserEntity user = new UserEntity(5L, "user", "passFindById", null, null, null, LocalDate.now());
		Long id = jdbcUserRepository.saveUser(user);

		// when
		UserEntity findUser = jdbcUserRepository.findById(id);

		// then
		assertThat(findUser.getId()).isEqualTo(id);
		assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
	}
}
