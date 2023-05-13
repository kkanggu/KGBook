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
		Long isbn = bookService.saveBook(book);
		return isbn;
	}

	@Override
	public List<BookEntity> findAll() {
		List<BookEntity> books = bookService.findAll();
		return books;
	}

	@Override
	public BookEntity findByIsbn(long isbn) {
		BookEntity book = bookService.findByIsbn(isbn);
		return book;
	}
}
