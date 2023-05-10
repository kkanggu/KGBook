package kkanggu.KGBook.book.repository;

import java.util.List;

import kkanggu.KGBook.book.entity.BookEntity;

public interface BookRepository {
	Long saveBook(BookEntity book);

	List<BookEntity> findAll();
}
