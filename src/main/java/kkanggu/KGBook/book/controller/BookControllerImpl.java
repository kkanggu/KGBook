package kkanggu.KGBook.book.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import kkanggu.KGBook.book.entity.BookEntity;
import kkanggu.KGBook.book.service.BookService;

@Controller
public class BookControllerImpl implements BookController {
	private final BookService bookService;

	public BookControllerImpl(BookService bookService) {
		this.bookService = bookService;
	}

	@Override
	public Long saveBook(BookEntity book) {
		return bookService.saveBook(book);
	}

	@Override
	public List<BookEntity> findAll() {
		return bookService.findAll();
	}

	@Override
	public BookEntity findByIsbn(long isbn) {
		return bookService.findByIsbn(isbn);
	}

	@Override
	public List<BookEntity> findBooksUserOwn(long userId) {
		List<Long> isbns = bookService.findIsbnByUserId(userId);

		return bookService.findByIsbn(isbns);
	}
}
