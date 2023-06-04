package kkanggu.KGBook.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import kkanggu.KGBook.user.entity.UserEntity;

@ExtendWith(MockitoExtension.class)
class JdbcUserRepositoryTest {
	private JdbcUserRepository repository;

	@Mock
	private JdbcTemplate jdbcTemplate;

	private final Long id = 13L;

	@BeforeEach
	void init() {
		lenient().when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(id);
		repository = new JdbcUserRepository(jdbcTemplate);
	}

	private UserEntity getUserEntity(Long id) {
		return UserEntity.builder()
				.id(id)
				.username("user")
				.password("password")
				.gender("M")
				.age(1)
				.birth(LocalDate.of(2000, 1, 1))
				.createDate(LocalDate.now())
				.build();
	}

	@Test
	@DisplayName("유저의 최대 id 가져오기 성공")
	void getMaxIdOk() {
		Long maxId = repository.getMaxId();

		assertAll(
				() -> assertThat(maxId).isNotNull(),
				() -> assertThat(maxId).isEqualTo(id)
		);
	}

	@Test
	@DisplayName("유저의 최대 id 가져오기, 유저가 없을 경우")
	void getMaxIdEmpty() {
		when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(null);
		repository = new JdbcUserRepository(jdbcTemplate);

		Long maxId = repository.getMaxId();

		assertAll(
				() -> assertThat(maxId).isNotNull(),
				() -> assertThat(maxId).isEqualTo(0)
		);
	}

	@Test
	@DisplayName("유저 저장 성공")
	void saveUserOk() {
		UserEntity user = getUserEntity(null);
		when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(1);

		Long newId = repository.saveUser(user);

		assertAll(
				() -> assertThat(newId).isNotNull(),
				() -> assertThat(newId).isEqualTo(id + 1)
		);
	}

	@Test
	@DisplayName("유저 저장 실패")
	void saveUserFail() {
		UserEntity user = getUserEntity(null);
		when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object[]>any())).thenReturn(0);

		Long newId = repository.saveUser(user);

		assertAll(
				() -> assertThat(newId).isNull()
		);
	}

	@Test
	@DisplayName("전체 유저 가져오기 성공")
	void findAllOk() {
		List<UserEntity> users = new ArrayList<>();
		UserEntity user = getUserEntity(135L);
		users.add(user);
		when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);

		List<UserEntity> findUsers = repository.findAll();

		assertAll(
				() -> assertThat(findUsers).isNotNull(),
				() -> assertThat(findUsers.size()).isEqualTo(users.size()),
				() -> IntStream.range(0, findUsers.size())
						.forEach(i -> assertThat(findUsers.get(i)).isEqualTo(users.get(i)))
		);
	}

	@Test
	@DisplayName("전체 유저 가져오기 성공, 아무것도 없을 경우")
	void findAllEmpty() {
		when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList());

		List<UserEntity> findUsers = repository.findAll();

		assertAll(
				() -> assertThat(findUsers).isNotNull(),
				() -> assertThat(findUsers.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Id를 이용하여 유저 가져오기 성공")
	void findByIdOk() {
		UserEntity user = getUserEntity(135L);
		when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any())).thenReturn(user);

		UserEntity findUser = repository.findById(user.getId());

		assertAll(
				() -> assertThat(findUser).isNotNull(),
				() -> assertThat(findUser).isEqualTo(user)
		);
	}

	@Test
	@DisplayName("Id를 이용하여 유저 가져오기 실패")
	void findByIdFail() {
		Long id = 13L;
		when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), any())).thenReturn(null);

		UserEntity findUser = repository.findById(id);

		assertAll(
				() -> assertThat(findUser).isNull()
		);
	}
}
