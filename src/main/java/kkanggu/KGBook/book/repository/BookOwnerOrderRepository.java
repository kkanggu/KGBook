package kkanggu.KGBook.book.repository;

import java.util.List;

public interface BookOwnerOrderRepository {
	void saveBookUserOwn(Long isbn, Long userId);

	List<Long> findIsbnByUserId(Long userId);

	List<Long> findUserIdByIsbn(Long isbn);
}
