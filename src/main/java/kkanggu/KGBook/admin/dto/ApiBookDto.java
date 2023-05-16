package kkanggu.KGBook.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiBookDto {
	private String title;
	private String link;
	private String image;
	private String author;
	private Integer discount;
	private String publisher;
	private String pubdate;
	private Long isbn;
	private String description;

	public ApiBookDto() {
	}
}
