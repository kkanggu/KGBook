package kkanggu.KGBook.book.repository;

import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import kkanggu.KGBook.book.dto.RenderBookDto;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.common.aws.ImageController;
import kkanggu.KGBook.sql.BookSql;

@Repository
public class JdbcBookRepository implements BookRepository {
	private final JdbcTemplate jdbcTemplate;
	private final ImageController imageController;

	@Autowired
	public JdbcBookRepository(DataSource dataSource,
							  ImageController imageController) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.imageController = imageController;
	}

	@Override
	public Long saveBook(BookEntity book) {
		String s3ImageUrl = imageController.uploadImage(book.getOriginImageUrl());

		if (null == s3ImageUrl) {
			return null;
		}

		Object[] params = {book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublishDate(),
				book.getCreateDate(), book.getDescription(), book.getOriginImageUrl(), s3ImageUrl};
		int rows = jdbcTemplate.update(BookSql.CREATE_BOOK, params);
		return 1 == rows ? book.getIsbn() : null;
	}

	@Override
	public List<BookEntity> findAll() {
		return jdbcTemplate.query(BookSql.SELECT_BOOKS, rowMapper());
	}

	@Override
	public BookEntity findByIsbn(Long isbn) {
		return jdbcTemplate.queryForObject(BookSql.SELECT_BOOKS_BY_ISBN, rowMapper(), isbn);
	}

	@Override
	public void updateBook(RenderBookDto book) {
		Object[] params = {book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublishDate(),
				book.getDescription(), book.getIsbn()};
		jdbcTemplate.update(BookSql.UPDATE_BOOK, params);
	}

	private RowMapper<BookEntity> rowMapper() {
		return (rs, rowNum) -> BookEntity.builder()
				.isbn(rs.getLong("isbn"))
				.title(rs.getString("title"))
				.author(rs.getString("author"))
				.publisher(rs.getString("publisher"))
				.publishDate(rs.getTimestamp("publish_date").toLocalDateTime().toLocalDate())
				.createDate(LocalDate.now())
				.description(rs.getString("description"))
				.originImageUrl(rs.getString("original_image_url"))
				.s3ImageUrl(rs.getString("s3_image_url"))
				.build();
	}
}
