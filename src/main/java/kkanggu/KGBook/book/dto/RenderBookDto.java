package kkanggu.KGBook.book.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenderBookDto {
	private Long isbn;
	private String title;
	private String author;
	private String publisher;
	private LocalDate publishDate;
	private String description;
	private String imageUrl;

	public RenderBookDto() {
	}
}
