package com.bookstore.repository;

import com.bookstore.domain.BookToCartItem;
import com.bookstore.domain.CartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

@Transactional
public interface BookToCartItemRepository extends CrudRepository<BookToCartItem, Long> {

    void deleteByCartItem(@NonNull CartItem cartItem);
}