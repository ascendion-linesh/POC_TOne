package com.bookstore.repository;

import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.Order;
import com.bookstore.domain.ShoppingCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends CrudRepository<CartItem, Long> {

    // Find all CartItems associated with a given ShoppingCart
    @NonNull
    List<CartItem> findByShoppingCart(@NonNull ShoppingCart shoppingCart);

    // Find all CartItems associated with a given Order
    @NonNull
    List<CartItem> findByOrder(@NonNull Order order);

    // Find CartItem associated with both ShoppingCart and Book (returns Optional)
    Optional<CartItem> findByShoppingCartAndBook(ShoppingCart shoppingCart, Book book);

}
