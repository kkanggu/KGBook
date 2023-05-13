package kkanggu.KGBook.user.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import kkanggu.KGBook.sql.BookSql;
import kkanggu.KGBook.user.entity.UserEntity;

@Repository
public class JdbcUserRepository implements UserRepository {
	private final JdbcTemplate jdbcTemplate;
	private Long id;

	public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.id = getMaxId();
	}

	@Override
	public Long getMaxId() {
		String query = BookSql.SELECT_MAX_ID_FROM + "USER";
		Long id = jdbcTemplate.queryForObject(query, Long.class);

		return null != id ? id : 0;
	}

	@Override
	public Long saveUser(UserEntity user) {
		Object[] params = {++id, user.getUsername(), user.getPassword(), user.getGender(),
				user.getAge(), user.getBirth(), LocalDate.now()};
		jdbcTemplate.update(BookSql.CREATE_USER, params);
		return id;
	}

	@Override
	public List<UserEntity> findAll() {
		List<UserEntity> users = jdbcTemplate.query(BookSql.SELECT_USERS, rowMapper());
		return users;
	}

	@Override
	public UserEntity findById(Long id) {
		UserEntity user = jdbcTemplate.queryForObject(BookSql.SELECT_USER_BY_ID, rowMapper(), id);
		return user;
	}

	private RowMapper<UserEntity> rowMapper() {
		return (rs, rowNum) -> {
			UserEntity user = new UserEntity();
			user.setId(rs.getLong("id"));
			user.setUsername(rs.getString("username"));
			user.setPassword(rs.getString("password"));
			user.setGender(rs.getString("gender"));
			user.setAge(rs.getInt("age"));
			user.setBirth(rs.getString("birth"));
			user.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime().toLocalDate());

			return user;
		};
	}
}
