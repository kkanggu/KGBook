package kkanggu.KGBook.book.controller;

import java.util.List;

import kkanggu.KGBook.book.entity.BookEntity;

public interface BookController {
	List<BookEntity> findAll();

	BookEntity findByIsbn(long isbn);
}
