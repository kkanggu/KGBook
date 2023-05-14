package kkanggu.KGBook.book.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.repository.BookOwnerOrderRepository;
import kkanggu.KGBook.book.repository.BookRepository;

@Service
public class BookService {
	private final BookRepository bookRepository;
	private final BookOwnerOrderRepository bookOwnerOrderRepository;

	public BookService(BookRepository bookRepository,
					   BookOwnerOrderRepository bookOwnerOrderRepository) {
		this.bookRepository = bookRepository;
		this.bookOwnerOrderRepository = bookOwnerOrderRepository;
	}

	public Long saveBook(BookEntity book) {
		return bookRepository.saveBook(book);
	}

	public List<BookEntity> findAll() {
		return bookRepository.findAll();
	}

	public BookEntity findByIsbn(long isbn) {
		return bookRepository.findByIsbn(isbn);
	}

	public List<Long> findIsbnByUserId(long userId) {
		return bookOwnerOrderRepository.findIsbnByUserId(userId);
	}

	/**
	 * If user own 10K, then 10K query will execute
	 * This can lower performance
	 */
	public List<BookEntity> findByIsbn(List<Long> isbns) {
		List<BookEntity> books = new ArrayList<>();
		for (Long isbn : isbns) {
			BookEntity book = bookRepository.findByIsbn(isbn);
			books.add(book);
		}

		return books;
	}
}
