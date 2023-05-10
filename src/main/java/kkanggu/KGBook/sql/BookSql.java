package kkanggu.KGBook.sql;

public class BookSql {
	public static String CREATE_BOOK = "INSERT INTO BOOK (id, title, author, publisher, publish_date, isbn, description) VALUES (?, ?, ?, ?, ?, ?, ?);";
	public static String UPDATE_BOOK = "UPDATE BOOK SET title=?, author=?, publisher=?, publish_date=?, isbn=?, description=? WHERE id=?;";
	public static String SELECT_BOOKS = "SELECT * FROM BOOK";
	public static String SELECT_BOOKS_BY_ID = "SELECT * FROM BOOK WHERE id = ?";
	public static String SELECT_BOOKS_BY_ISBN = "SELECT * FROM BOOK WHERE isbn = ?";
	public static String SELECT_MAX_ID = "SELECT MAX(id) FROM BOOK";
}
