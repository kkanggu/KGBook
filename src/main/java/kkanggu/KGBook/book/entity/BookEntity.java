package kkanggu.KGBook.book.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookEntity {
	private Long id;
	private String title;
	private String author;
	private String publisher;
	private LocalDate publishDate;
	private String isbn;
	private String description;

	public BookEntity() {
	}

	public BookEntity(String title, String author, String publisher, LocalDate publishDate, String isbn, String description) {
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.publishDate = publishDate;
		this.isbn = isbn;
		this.description = description;
	}
}
