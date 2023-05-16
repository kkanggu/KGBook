package kkanggu.KGBook.admin.controller;

import java.util.List;

import kkanggu.KGBook.book.entity.BookEntity;

public interface AdminController {
	List<BookEntity> searchBooks(String keyword, boolean searchRecent);

	void saveBooks(List<BookEntity> books);
}
