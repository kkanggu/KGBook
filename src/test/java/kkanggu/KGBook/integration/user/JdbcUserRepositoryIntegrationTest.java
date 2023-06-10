package kkanggu.KGBook.integration.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.repository.JdbcUserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class JdbcUserRepositoryIntegrationTest {
	@Autowired
	private JdbcUserRepository jdbcUserRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void init() {
		jdbcTemplate.update("DELETE FROM BOOK");
	}

	private UserEntity getUserEntity() {
		return UserEntity.builder()
				.id(null)
				.username("user")
				.password("password")
				.gender("M")
				.age(1)
				.birth(LocalDate.of(2000, 1, 1))
				.createDate(LocalDate.now())
				.build();
	}

	@Test
	@DisplayName("Id 최대값 가져오기 성공, 유저가 없을 경우 0")
	void getMaxIdOk() {
		Long maxId = jdbcUserRepository.getMaxId();

		assertAll(
				() -> assertThat(maxId).isNotNull(),
				() -> assertThat(maxId).isEqualTo(0)
		);
	}

	@Test
	@DisplayName("Id 최대값 가져오기 성공, 유저가 있을 경우 1 이상")
	void getMaxIdMoreThan0() {
		int count = 3;
		IntStream.range(0, count)
				.forEach(i -> jdbcUserRepository.saveUser(getUserEntity()));

		Long maxId = jdbcUserRepository.getMaxId();

		assertAll(
				() -> assertThat(maxId).isNotNull(),
				() -> assertThat(maxId).isEqualTo(count)
		);
	}

	@Test
	@DisplayName("유저 저장 성공")
	void saveUserOk() {
		UserEntity user = getUserEntity();

		Long savedId = jdbcUserRepository.saveUser(user);

		assertAll(
				() -> assertThat(savedId).isNotNull()
		);
	}

	@Test
	@DisplayName("전체 유저 가져오기 성공")
	void findAllOk() {
		int count = 4;
		IntStream.range(0, count)
				.forEach(i -> jdbcUserRepository.saveUser(getUserEntity()));

		List<UserEntity> findUser = jdbcUserRepository.findAll();

		assertAll(
				() -> assertThat(findUser).isNotNull(),
				() -> assertThat(findUser.size()).isEqualTo(count)
		);
	}

	@Test
	@DisplayName("전체 유저 가져오기 성공, 유저가 없을 경우")
	void findAllEmpty() {
		List<UserEntity> findUser = jdbcUserRepository.findAll();

		assertAll(
				() -> assertThat(findUser).isNotNull(),
				() -> assertThat(findUser.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Id를 이용하여 유저 가져오기 성공")
	void findByIdOk() {
		Long savedId = jdbcUserRepository.saveUser(getUserEntity());

		UserEntity findUser = jdbcUserRepository.findById(savedId);

		assertAll(
				() -> assertThat(findUser).isNotNull(),
				() -> assertThat(findUser.getId()).isEqualTo(savedId)
		);
	}

	@Test
	@DisplayName("Id를 이용하여 유저 가져오기 성공, 유저가 없을 경우")
	void findByIdNotFound() {
		Long id = 13L;

		UserEntity findUser = jdbcUserRepository.findById(id);

		assertAll(
				() -> assertThat(findUser).isNull()
		);
	}
}
