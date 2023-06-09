package kkanggu.KGBook.integration.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.repository.BookOwnerOrderRepository;
import kkanggu.KGBook.book.service.BookService;
import kkanggu.KGBook.common.aws.ImageController;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {
	@Autowired
	private UserService userService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private BookService bookService;

	@Autowired
	private ImageController imageController;

	@Autowired
	private BookOwnerOrderRepository bookOwnerOrderRepository;

	@BeforeEach
	void init() {
		jdbcTemplate.update("DELETE FROM USER");
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

	private BookEntity getBookEntity(Long isbn, String title) {
		return BookEntity.builder()
				.isbn(isbn)
				.title(title)
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.of(2016, 1, 1))
				.createDate(LocalDate.now())
				.description("description")
				.originImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
	}

	private void deleteImage() {
		List<String> s3ImageUrls = jdbcTemplate.query("SELECT s3_image_url FROM BOOK", (rs, rowNum) -> rs.getString("s3_image_url"));
		for (String s3ImageUrl : s3ImageUrls) {
			imageController.deleteImage(s3ImageUrl);
		}
	}

	@Test
	@DisplayName("유저 저장 성공")
	void saveUserOk() {
		UserEntity user = getUserEntity();

		Long savedId = userService.saveUser(user);

		assertAll(
				() -> assertThat(savedId).isNotNull()
		);
	}

	@Test
	@DisplayName("전체 유저 가져오기 성공")
	void findAllOk() {
		userService.saveUser(getUserEntity());

		List<UserEntity> users = userService.findAll();

		assertAll(
				() -> assertThat(users).isNotNull(),
				() -> assertThat(users.size()).isEqualTo(1)
		);
	}

	@Test
	@DisplayName("전체 유저 가져오기 성공, 유저가 없을 경우")
	void findAllEmpty() {
		List<UserEntity> users = userService.findAll();

		assertAll(
				() -> assertThat(users).isNotNull(),
				() -> assertThat(users.isEmpty()).isTrue()
		);
	}

	@Test
	@DisplayName("Id를 이용하여 유저 가져오기 성공")
	void findByIdOk() {
		Long savedId = userService.saveUser(getUserEntity());

		UserEntity findUser = userService.findById(savedId);

		assertAll(
				() -> assertThat(findUser).isNotNull()
		);
	}

	@Test
	@DisplayName("Id를 이용하여 유저 가져오기 실패, 유저가 없을 경우")
	void findByIdNotFound() {
		Long id = 0L;

		UserEntity findUser = userService.findById(id);

		assertAll(
				() -> assertThat(findUser).isNull()
		);
	}

	@Test
	@DisplayName("특정 서적을 보유중인 유저 서적 가져오기 성공")
	void findUsersHaveBookOk() {
		BookEntity book = getBookEntity(1L, "title");
		bookService.saveBook(book);
		Long userId1 = userService.saveUser(getUserEntity());
		Long userId2 = userService.saveUser(getUserEntity());
		bookOwnerOrderRepository.saveBookUserOwn(book.getIsbn(), userId1);
		bookOwnerOrderRepository.saveBookUserOwn(book.getIsbn(), userId2);

		List<UserEntity> findUsers = userService.findUsersHaveBook(book.getIsbn());

		assertAll(
				() -> assertThat(findUsers).isNotNull(),
				() -> assertThat(findUsers.stream()
						.map(UserEntity::getId)
						.toList()).contains(userId1, userId2)
		);
		deleteImage();
	}

	@Test
	@DisplayName("특정 서적을 보유중인 유저 서적 가져오기 실패, 유저나 서적이 없을 경우")
	void findUsersHaveBookEmpty() {
		Long isbn = 13L;

		List<UserEntity> findUsers = userService.findUsersHaveBook(isbn);

		assertAll(
				() -> assertThat(findUsers).isNotNull(),
				() -> assertThat(findUsers.isEmpty()).isTrue()
		);
	}
}
