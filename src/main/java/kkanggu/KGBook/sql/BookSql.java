package kkanggu.KGBook.sql;

public class BookSql {
	public static String CREATE_BOOK = "INSERT INTO BOOK (isbn, title, author, publisher, publish_date," +
			"description, original_image_url, s3_image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
	public static String UPDATE_BOOK = "UPDATE BOOK SET title=?, author=?, publisher=?, publish_date=?," +
			"description=?, original_image_url=?, s3_image_url=? WHERE isbn=?;";
	public static String SELECT_BOOKS = "SELECT * FROM BOOK";
	public static String SELECT_BOOKS_BY_ISBN = "SELECT * FROM BOOK WHERE isbn = ?";
}
