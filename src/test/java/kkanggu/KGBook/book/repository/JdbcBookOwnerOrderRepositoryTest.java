package kkanggu.KGBook.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@ExtendWith(MockitoExtension.class)
class JdbcBookOwnerOrderRepositoryTest {
	@InjectMocks
	private JdbcBookOwnerOrderRepository repository;

	@Mock
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("유저 id를 이용하여 유저가 보유한 서적의 isbn 가져오기 성공")
	void findIsbnByUserIdOk() {
		Long userId = 23L;
		List<Long> isbns = new ArrayList<>();
		isbns.add(13L);
		isbns.add(35L);
		when(jdbcTemplate.query(anyString(), any(RowMapper.class), ArgumentMatchers.<Object[]>any())).thenReturn(isbns);

		List<Long> findIsbns = repository.findIsbnByUserId(userId);

		assertAll(
				() -> assertThat(findIsbns).isEqualTo(isbns)
		);
	}

	@Test
	@DisplayName("유저 id를 이용하여 유저가 보유한 서적의 isbn 가져오기, 없을 경우")
	void findIsbnByUserIdEmpty() {
		Long userId = 23L;
		when(jdbcTemplate.query(anyString(), any(RowMapper.class), ArgumentMatchers.<Object[]>any())).thenReturn(new ArrayList());

		List<Long> findIsbns = repository.findIsbnByUserId(userId);

		assertAll(
				() -> assertThat(findIsbns).isNotNull(),
				() -> assertThat(findIsbns.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 해당 서적을 보유한 유저의 id 가져오기 성공")
	void findUserIdByIsbnOk() {
		Long isbn = 23L;
		List<Long> userIds = new ArrayList<>();
		userIds.add(13L);
		userIds.add(35L);
		when(jdbcTemplate.query(anyString(), any(RowMapper.class), ArgumentMatchers.<Object[]>any())).thenReturn(userIds);

		List<Long> findUserIds = repository.findUserIdByIsbn(isbn);

		assertAll(
				() -> assertThat(findUserIds).isEqualTo(userIds)
		);
	}

	@Test
	@DisplayName("Isbn을 이용하여 해당 서적을 보유한 유저의 id 가져오기, 없을 경우")
	void findUserIdByIsbnEmpty() {
		Long isbn = 23L;
		when(jdbcTemplate.query(anyString(), any(RowMapper.class), ArgumentMatchers.<Object[]>any())).thenReturn(new ArrayList());

		List<Long> findUserIds = repository.findUserIdByIsbn(isbn);

		assertAll(
				() -> assertThat(findUserIds).isNotNull(),
				() -> assertThat(findUserIds.isEmpty()).isTrue()
		);
	}

}
