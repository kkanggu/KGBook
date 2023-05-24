package kkanggu.KGBook.book.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RenderBookDto {
	private Long isbn;
	private String title;
	private String author;
	private String publisher;
	private Integer originPrice;
	private Integer discountPrice;
	private Integer discountRate;
	private String discountType;
	private LocalDate publishDate;
	private String description;
	private String imageUrl;

	public RenderBookDto(Long isbn, String title, String author, String publisher, Integer originPrice, Integer discountPrice, Integer discountRate, String discountType, LocalDate publishDate, String description, String imageUrl) {
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.publisher = publisher;
		this.originPrice = originPrice;
		this.discountPrice = discountPrice;
		this.discountRate = discountRate;
		this.discountType = discountType;
		this.publishDate = publishDate;
		this.description = description;
		this.imageUrl = imageUrl;
	}
}
