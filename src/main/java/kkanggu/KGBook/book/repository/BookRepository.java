package kkanggu.KGBook.book.repository;

import java.util.List;

import kkanggu.KGBook.book.entity.BookEntity;

public interface BookRepository {
	int saveBook(BookEntity book);

	List<BookEntity> findAll();

	BookEntity findByIsbn(Long isbn);
}
