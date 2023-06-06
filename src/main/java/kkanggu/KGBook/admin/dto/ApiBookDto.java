package kkanggu.KGBook.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
}
