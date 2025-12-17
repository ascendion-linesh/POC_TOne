package com.bookstore.service.impl;

import com.bookstore.domain.*;
import com.bookstore.repository.BookToCartItemRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.service.CartItemService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final BookToCartItemRepository bookToCartItemRepository;

    public CartItemServiceImpl(CartItemRepository cartItemRepository,
                               BookToCartItemRepository bookToCartItemRepository) {
        this.cartItemRepository = Objects.requireNonNull(cartItemRepository);
        this.bookToCartItemRepository = Objects.requireNonNull(bookToCartItemRepository);
    }

    @Override
    public List<CartItem> findByShoppingCart(ShoppingCart shoppingCart) {
        Objects.requireNonNull(shoppingCart);
        return cartItemRepository.findByShoppingCart(shoppingCart);
    }

    @Override
    public CartItem updateCartItem(CartItem cartItem) {
        Objects.requireNonNull(cartItem);
        BigDecimal subtotal = cartItem.getBook().getOurPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQty()))  // Convert qty to BigDecimal
                .setScale(2, RoundingMode.HALF_UP);

        cartItem.setSubtotal(subtotal);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public CartItem addBookToCartItem(Book book, User user, int qty) {
        ShoppingCart shoppingCart = user.getShoppingCart();

        // Look for an existing CartItem with the same book and cart
        Optional<CartItem> optionalCartItem = cartItemRepository.findByShoppingCartAndBook(shoppingCart, book);

        CartItem cartItem;
        if (optionalCartItem.isPresent()) {
            // Update existing item
            cartItem = optionalCartItem.get();
            cartItem.setQty(cartItem.getQty() + qty);
        } else {
            // Create new item
            cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setQty(qty);
            cartItem.setShoppingCart(shoppingCart);
        }

        // Always update subtotal
        cartItem.setSubtotal(book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQty())));

        // Save or update
        return cartItemRepository.save(cartItem);
    }


    // Helper method to check if a cart item already exists
    private CartItem findExistingCartItem(User user, Book book) {
        ShoppingCart cart = user.getShoppingCart();
        if (cart == null) return null;

        return cartItemRepository.findByShoppingCartAndBook(cart, book).orElse(null);
    }

    // Helper method to update the cart item's quantity and subtotal
    private CartItem updateCartItemQuantity(CartItem cartItem, int qty) {
        int updatedQty = cartItem.getQty() + qty;  // Update the quantity
        cartItem.setQty(updatedQty);

        // Recalculate the subtotal with the updated quantity
        BigDecimal subtotal = cartItem.getBook().getOurPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQty()))  // Convert qty to BigDecimal
                .setScale(2, RoundingMode.HALF_UP);
        cartItem.setSubtotal(subtotal);

        // Save and return the updated cart item
        return cartItemRepository.save(cartItem);
    }

    // Helper method to create a new CartItem
    private CartItem createNewCartItem(Book book, User user, int qty) {
        ShoppingCart cart = user.getShoppingCart();

        // Check if the item already exists in the cart
        Optional<CartItem> optionalCartItem = cartItemRepository.findByShoppingCartAndBook(cart, book);

        CartItem cartItem;

        if (optionalCartItem.isPresent()) {
            // Item already exists - update it
            cartItem = optionalCartItem.get();
            int updatedQty = cartItem.getQty() + qty;
            cartItem.setQty(updatedQty);

            BigDecimal subtotal = cartItem.getBook().getOurPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQty()))  // Convert qty to BigDecimal
                    .setScale(2, RoundingMode.HALF_UP);
            cartItem.setSubtotal(subtotal);
        } else {
            // New item - create it
            cartItem = new CartItem();
            cartItem.setShoppingCart(cart);
            cartItem.setBook(book);
            cartItem.setQty(qty);

            BigDecimal subtotal = cartItem.getBook().getOurPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQty()))  // Convert qty to BigDecimal
                    .setScale(2, RoundingMode.HALF_UP);
            cartItem.setSubtotal(subtotal);
        }

        // Save or update the cart item
        cartItem = cartItemRepository.save(cartItem);

        // Save BookToCartItem mapping only if it's new
        if (!optionalCartItem.isPresent()) {
            BookToCartItem bookToCartItem = new BookToCartItem();
            bookToCartItem.setBook(book);
            bookToCartItem.setCartItem(cartItem);
            bookToCartItemRepository.save(bookToCartItem);
        }

        return cartItem;
    }


    @Override
    public CartItem findById(Long id) {
        Objects.requireNonNull(id);
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CartItem not found with id: " + id));
    }

    @Override
    public void removeCartItem(CartItem cartItem) {
        Objects.requireNonNull(cartItem);
        bookToCartItemRepository.deleteByCartItem(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    public CartItem save(CartItem cartItem) {
        Objects.requireNonNull(cartItem);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> findByOrder(Order order) {
        Objects.requireNonNull(order);
        return cartItemRepository.findByOrder(order);
    }
}
