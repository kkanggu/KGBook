package kkanggu.KGBook.book.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.common.aws.ImageController;
import kkanggu.KGBook.sql.BookSql;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class JdbcBookRepository implements BookRepository {
	private final JdbcTemplate jdbcTemplate;
	private final ImageController imageController;

	@Autowired
	public JdbcBookRepository(JdbcTemplate jdbcTemplate,
							  ImageController imageController) {
		this.jdbcTemplate = jdbcTemplate;
		this.imageController = imageController;
	}

	@Override
	public Long saveBook(BookEntity book) {
		String s3ImageUrl = imageController.uploadImage(book.getOriginImageUrl());

		if (null == s3ImageUrl) {
			return null;
		}

		Object[] params = {book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(),
				book.getOriginPrice(), book.getDiscountPrice(), book.getDiscountRate(), book.getDiscountType(),
				book.getPublishDate(), book.getCreateDate(), book.getDescription(), book.getOriginImageUrl(),
				s3ImageUrl};
		int rows = jdbcTemplate.update(BookSql.CREATE_BOOK, params);
		return 1 == rows ? book.getIsbn() : null;
	}

	@Override
	public List<BookEntity> findAll() {
		return jdbcTemplate.query(BookSql.SELECT_BOOKS, rowMapper());
	}

	@Override
	public BookEntity findByIsbn(Long isbn) {
		BookEntity book = null;

		try {
			book = jdbcTemplate.queryForObject(BookSql.SELECT_BOOKS_BY_ISBN, rowMapper(), isbn);
		} catch (EmptyResultDataAccessException e) {
			log.info("No book exist with isbn {}", isbn);
		}

		return book;
	}

	@Override
	public void updateBook(BookEntity book) {
		Object[] params = {book.getTitle(), book.getAuthor(), book.getPublisher(), book.getOriginPrice(),
				book.getDiscountPrice(), book.getDiscountRate(), book.getDiscountType(),
				book.getPublishDate(), book.getDescription(), book.getIsbn()};
		jdbcTemplate.update(BookSql.UPDATE_BOOK, params);
	}

	private RowMapper<BookEntity> rowMapper() {
		return (rs, rowNum) -> BookEntity.builder()
				.isbn(rs.getLong("isbn"))
				.title(rs.getString("title"))
				.author(rs.getString("author"))
				.publisher(rs.getString("publisher"))
				.originPrice(rs.getInt("original_price"))
				.discountPrice(rs.getInt("discount_price"))
				.discountRate(rs.getInt("discount_rate"))
				.discountType(rs.getString("discount_type"))
				.publishDate(rs.getTimestamp("publish_date").toLocalDateTime().toLocalDate())
				.createDate(LocalDate.now())
				.description(rs.getString("description"))
				.originImageUrl(rs.getString("original_image_url"))
				.s3ImageUrl(rs.getString("s3_image_url"))
				.build();
	}
}
