package kkanggu.KGBook.book.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookEntity {
	private Long isbn;
	private String title;
	private String author;
	private String publisher;
	private LocalDate publishDate;
	private LocalDate createDate;
	private String description;
	private String originImageUrl;
	private String s3ImageUrl;

	public BookEntity() {
	}

	public BookEntity(Long isbn, String title, String author, String publisher, LocalDate publishDate,
					  LocalDate createDate, String description, String originImageUrl, String s3ImageUrl) {
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.publishDate = publishDate;
		this.createDate = createDate;
		this.description = description;
		this.originImageUrl = originImageUrl;
		this.s3ImageUrl = s3ImageUrl;
	}
}
