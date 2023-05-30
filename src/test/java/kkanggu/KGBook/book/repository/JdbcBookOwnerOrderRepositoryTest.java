package kkanggu.KGBook.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.service.BookService;
import kkanggu.KGBook.common.aws.ImageController;
import kkanggu.KGBook.user.entity.UserEntity;
import kkanggu.KGBook.user.service.UserService;


@SpringBootTest
@ActiveProfiles("local")
@Transactional
class JdbcBookOwnerOrderRepositoryTest {
	private final JdbcTemplate jdbcTemplate;
	private final ImageController imageController;
	private final BookService bookService;
	private final UserService userService;
	private final JdbcBookOwnerOrderRepository jdbcBookOwnerOrderRepository;
	private final BookEntity book;
	private final List<UserEntity> users;

	@Autowired
	public JdbcBookOwnerOrderRepositoryTest(DataSource dataSource,
											BookService bookService,
											UserService userService,
											ImageController imageController,
											JdbcBookOwnerOrderRepository jdbcBookOwnerOrderRepository) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.bookService = bookService;
		this.userService = userService;
		this.imageController = imageController;
		this.jdbcBookOwnerOrderRepository = jdbcBookOwnerOrderRepository;
		book = BookEntity.builder()
				.isbn(1L)
				.title("title")
				.author("author")
				.publisher("publisher")
				.originPrice(13579)
				.publishDate(LocalDate.now())
				.createDate(LocalDate.now())
				.originImageUrl("https://shopping-phinf.pstatic.net/main_3249079/32490791688.20221230074134.jpg")
				.build();
		users = new ArrayList<>();
		users.add(new UserEntity(1L, "username", "pass", null, null, null, LocalDate.now()));
		users.add(new UserEntity(2L, "username", "pass", null, null, null, LocalDate.now()));
	}

	@BeforeEach
	void clear() {
		jdbcTemplate.update("DELETE FROM BOOK_USER_OWN");
	}

	@AfterEach
	void removeImage() {
		BookEntity savedBook = bookService.findByIsbn(book.getIsbn());
		imageController.deleteImage(savedBook.getS3ImageUrl());
	}

	@Test
	@DisplayName("유저가 보유한 서적 저장")
	void saveBookUserOwnTest() {
		// given
		bookService.saveBook(book);
		Long id1 = userService.saveUser(users.get(0));
		Long id2 = userService.saveUser(users.get(1));

		// when
		jdbcBookOwnerOrderRepository.saveBookUserOwn(book.getIsbn(), id1);
		jdbcBookOwnerOrderRepository.saveBookUserOwn(book.getIsbn(), id2);
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM BOOK_USER_OWN", Integer.class);

		// then
		assertThat(count).isEqualTo(2);

		removeImage();
	}

	@Test
	@DisplayName("유저 id를 이용하여 유저가 보유한 서적의 isbn 가져오기")
	void findIsbnByUserIdTest() {
		// given
		bookService.saveBook(book);
		Long id1 = userService.saveUser(users.get(0));
		Long id2 = userService.saveUser(users.get(1));
		jdbcBookOwnerOrderRepository.saveBookUserOwn(book.getIsbn(), id1);
		jdbcBookOwnerOrderRepository.saveBookUserOwn(book.getIsbn(), id2);

		// when
		List<Long> isbnByUserId1 = jdbcBookOwnerOrderRepository.findIsbnByUserId(id1);
		List<Long> isbnByUserId2 = jdbcBookOwnerOrderRepository.findIsbnByUserId(id2);


		System.out.println("id1 = " + id1);
		System.out.println("id1 = " + id2);

		// then
		assertThat(isbnByUserId1).isNotNull();
		assertThat(isbnByUserId1.size()).isEqualTo(1);
		assertThat(isbnByUserId1.get(0)).isEqualTo(book.getIsbn());
		assertThat(isbnByUserId2).isNotNull();
		assertThat(isbnByUserId2.size()).isEqualTo(1);
		assertThat(isbnByUserId2.get(0)).isEqualTo(book.getIsbn());

		removeImage();
	}

	@Test
	@DisplayName("isbn을 이용하여 해당 서적을 보유한 유저들의 id 가져오기")
	void findUserIdByIsbnTest() {
		// given
		bookService.saveBook(book);
		Long id1 = userService.saveUser(users.get(0));
		Long id2 = userService.saveUser(users.get(1));
		jdbcBookOwnerOrderRepository.saveBookUserOwn(book.getIsbn(), id1);
		jdbcBookOwnerOrderRepository.saveBookUserOwn(book.getIsbn(), id2);

		// when
		List<Long> userIdByIsbn = jdbcBookOwnerOrderRepository.findUserIdByIsbn(book.getIsbn());

		// then
		assertThat(userIdByIsbn).isNotNull();
		assertThat(userIdByIsbn.size()).isEqualTo(2);
		assertThat(userIdByIsbn.get(0)).isEqualTo(id1);
		assertThat(userIdByIsbn.get(1)).isEqualTo(id2);

		removeImage();
	}
}
