package kkanggu.KGBook.book.controller;

import java.util.List;

import kkanggu.KGBook.book.entity.BookEntity;

public interface BookController {
	Long saveBook(BookEntity book);

	List<BookEntity> findAll();

	BookEntity findByIsbn(long isbn);

	List<BookEntity> findBooksUserOwn(long userId);
}
