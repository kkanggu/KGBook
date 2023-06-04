package kkanggu.KGBook.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import kkanggu.KGBook.book.repository.BookOwnerOrderRepository;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BookOwnerOrderRepository bookOwnerOrderRepository;

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
	@DisplayName("유저 저장 성공")
	void saveUserTestOk() {
		UserEntity user = getUserEntity(13L);
		when(userRepository.saveUser(user)).thenReturn(user.getId());

		Long id = service.saveUser(user);

		assertAll(
				() -> assertThat(id).isNotNull(),
				() -> assertThat(id).isEqualTo(user.getId())
		);
	}

	@Test
	@DisplayName("유저 저장 실패")
	void saveUserTestFail() {
		UserEntity user = getUserEntity(13L);
		when(userRepository.saveUser(user)).thenReturn(null);

		Long id = service.saveUser(user);

		assertAll(
				() -> assertThat(id).isNull()
		);
	}

	@Test
	@DisplayName("유저 전체 가져오기 성공")
	void findAllOk() {
		List<UserEntity> users = new ArrayList<>();
		users.add(getUserEntity(13L));
		users.add(getUserEntity(135L));
		users.add(getUserEntity(137L));

		when(userRepository.findAll()).thenReturn(users);

		List<UserEntity> findUsers = service.findAll();

		assertAll(
				() -> assertThat(findUsers).isNotNull(),
				() -> assertThat(findUsers.size()).isEqualTo(users.size()),
				() -> IntStream.range(0, findUsers.size())
						.forEach(i -> assertThat(findUsers.get(i)).isEqualTo(users.get(i)))
		);
	}

	@Test
	@DisplayName("유저 전체 가져오기 실패")
	void findAllEmpty() {
		when(userRepository.findAll()).thenReturn(new ArrayList<>());

		List<UserEntity> findUsers = service.findAll();

		assertAll(
				() -> assertThat(findUsers).isNotNull(),
				() -> assertThat(findUsers.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("id를 이용하여 유저 가져오기 성공")
	void findByIdOk() {
		UserEntity user = getUserEntity(13L);
		when(userRepository.findById(user.getId())).thenReturn(user);

		UserEntity findUser = service.findById(user.getId());

		assertAll(
				() -> assertThat(findUser).isNotNull(),
				() -> assertThat(findUser).isEqualTo(user)
		);
	}

	@Test
	@DisplayName("id를 이용하여 유저 가져오기, 해당 id를 가진 유저가 없을 경우")
	void findByIdCantFind() {
		when(userRepository.findById(0L)).thenReturn(null);

		UserEntity findUser = service.findById(0L);

		assertAll(
				() -> assertThat(findUser).isNull()
		);
	}
}
