package kkanggu.KGBook.book.entity;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookEntity {
	private Long isbn;
	private String title;
	private String author;
	private String publisher;
	private Integer originPrice;
	private Integer discountPrice;
	private Integer discountRate;
	private String discountType;
	private LocalDate publishDate;
	private LocalDate createDate;
	private String description;
	private String originImageUrl;
	private String s3ImageUrl;

	public BookEntity(Long isbn, String title, String author, String publisher, Integer originPrice, Integer discountPrice, Integer discountRate, String discountType, LocalDate publishDate, LocalDate createDate, String description, String originImageUrl, String s3ImageUrl) {
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.originPrice = originPrice;
		this.discountPrice = discountPrice;
		this.discountRate = discountRate;
		this.discountType = discountType;
		this.publishDate = publishDate;
		this.createDate = createDate;
		this.description = description;
		this.originImageUrl = originImageUrl;
		this.s3ImageUrl = s3ImageUrl;
	}
}
