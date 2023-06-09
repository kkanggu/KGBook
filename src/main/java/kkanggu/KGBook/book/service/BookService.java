package kkanggu.KGBook.book.service;

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

	public List<BookEntity> findBooksUserOwn(long userId) {
		List<Long> isbns = bookOwnerOrderRepository.findIsbnByUserId(userId);

		return isbns.stream()
				.map(bookRepository::findByIsbn)
				.toList();
	}

	public void updateBook(BookEntity book) {
		bookRepository.updateBook(book);
	}
}
