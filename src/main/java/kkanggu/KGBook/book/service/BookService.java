package kkanggu.KGBook.book.service;

import java.util.List;

import org.springframework.stereotype.Service;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.repository.BookRepository;

@Service
public class BookService {
	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public List<BookEntity> findAll() {
		List<BookEntity> books = bookRepository.findAll();
		return books;
	}

	public BookEntity findByIsbn(long isbn) {
		BookEntity book = bookRepository.findByIsbn(isbn);
		return book;
	}
}
