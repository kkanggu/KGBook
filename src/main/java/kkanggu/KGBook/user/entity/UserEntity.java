package kkanggu.KGBook.user.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEntity {
	private Long id;
	private String username;
	private String password;
	private String gender;
	private Integer age;
	private String birth;
	private LocalDate createDate;

	public UserEntity() {
	}

	public UserEntity(Long id, String username, String password, String gender, Integer age, String birth, LocalDate createDate) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.gender = gender;
		this.age = age;
		this.birth = birth;
		this.createDate = createDate;
	}
}
