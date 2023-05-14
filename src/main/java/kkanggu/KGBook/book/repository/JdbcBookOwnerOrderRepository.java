package kkanggu.KGBook.book.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import kkanggu.KGBook.sql.BookSql;

@Repository
public class JdbcBookOwnerOrderRepository implements BookOwnerOrderRepository {
	private final JdbcTemplate jdbcTemplate;

	public JdbcBookOwnerOrderRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void saveBookUserOwn(Long isbn, Long userId) {
		Object[] params = {isbn, userId};
		jdbcTemplate.update(BookSql.CREATE_BOOK_OWNER_ORDER, params);
	}

	@Override
	public List<Long> findIsbnByUserId(Long userId) {
		Object[] params = {userId};
		return jdbcTemplate.query(BookSql.SELECT_BOOKS_USER_OWN, rowMapper("isbn"), params);
	}

	@Override
	public List<Long> findUserIdByIsbn(Long isbn) {
		Object[] params = {isbn};
		return jdbcTemplate.query(BookSql.SELECT_USERS_BOOK_OWN, rowMapper("user_id"), params);
	}

	private RowMapper<Long> rowMapper(String target) {
		return (rs, rowNum) -> (Long) rs.getLong(target);
	}
}
