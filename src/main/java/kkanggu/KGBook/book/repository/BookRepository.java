package kkanggu.KGBook.book.repository;

import kkanggu.KGBook.book.entity.BookEntity;

public interface BookRepository {
	Long saveBook(BookEntity book);
}
