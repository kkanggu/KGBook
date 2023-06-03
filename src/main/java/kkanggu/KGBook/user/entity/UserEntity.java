package kkanggu.KGBook.user.entity;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserEntity {
	private Long id;
	private String username;
	private String password;
	private String gender;
	private Integer age;
	private LocalDate birth;
	private LocalDate createDate;

	public UserEntity(Long id, String username, String password, String gender, Integer age, LocalDate birth, LocalDate createDate) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.gender = gender;
		this.age = age;
		this.birth = birth;
		this.createDate = createDate;
	}
}
