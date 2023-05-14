package kkanggu.KGBook.sql;

public class BookSql {
	public static String CREATE_BOOK = "INSERT INTO BOOK (isbn, title, author, publisher, publish_date," +
			"description, original_image_url, s3_image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
	public static String UPDATE_BOOK = "UPDATE BOOK SET title=?, author=?, publisher=?, publish_date=?," +
			"description=?, original_image_url=?, s3_image_url=? WHERE isbn=?;";
	public static String SELECT_BOOKS = "SELECT * FROM BOOK";
	public static String SELECT_BOOKS_BY_ISBN = "SELECT * FROM BOOK WHERE isbn = ?";
	public static String CREATE_USER = "INSERT INTO USER (id, username, password, gender, age, birth, create_date)" +
			"VALUES (?, ?, ?, ?, ?, ?, ?)";
	public static String SELECT_MAX_ID_FROM = "SELECT MAX(id) FROM ";
	public static String SELECT_USERS = "SELECT * FROM USER";
	public static String SELECT_USER_BY_ID = "SELECT * FROM USER WHERE id = ?";
	public static String CREATE_BOOK_OWNER_ORDER = "INSERT INTO BOOK_USER_OWN (isbn, user_id) VALUES (?, ?)";
	public static String SELECT_BOOKS_USER_OWN = "SELECT isbn FROM BOOK_USER_OWN WHERE user_id = ? ORDER BY isbn";
	public static String SELECT_USERS_BOOK_OWN = "SELECT user_id FROM BOOK_USER_OWN WHERE isbn = ? ORDER BY user_id";
}
