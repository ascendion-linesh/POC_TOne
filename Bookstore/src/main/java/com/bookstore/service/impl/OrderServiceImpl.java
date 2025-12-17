package com.bookstore.service.impl;

import com.bookstore.domain.*;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.CartItemService;
import com.bookstore.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemService cartItemService;
    private final BookRepository bookRepository;

    public OrderServiceImpl(@NonNull OrderRepository orderRepository,
                            @NonNull CartItemService cartItemService,
                            @NonNull BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.cartItemService = cartItemService;
        this.bookRepository = bookRepository;
    }

    @Override
    public synchronized @NonNull Order createOrder(
            @NonNull ShoppingCart shoppingCart,
            @NonNull ShippingAddress shippingAddress,
            @NonNull BillingAddress billingAddress,
            @NonNull Payment payment,
            @NonNull String shippingMethod,
            @NonNull User user) {

        Order order = new Order();
        order.setBillingAddress(billingAddress);
        order.setOrderStatus("created");
        order.setPayment(payment);
        order.setShippingAddress(shippingAddress);
        order.setShippingMethod(shippingMethod);

        List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

        for (CartItem cartItem : cartItemList) {
            Book book = cartItem.getBook();
            cartItem.setOrder(order);
            int updatedStock = book.getInStockNumber() - cartItem.getQty();
            book.setInStockNumber(updatedStock);
            bookRepository.save(book);
        }

        order.setCartItemList(cartItemList);

        ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.systemDefault()); // Convert to ZonedDateTime using system default time zone
        order.setOrderDate(zonedDateTime.toLocalDateTime());
        order.setOrderTotal(shoppingCart.getGrandTotal());
        shippingAddress.setOrder(order);
        billingAddress.setOrder(order);
        payment.setOrder(order);
        order.setUser(user);

        return orderRepository.save(order);
    }

    @Override
    public @NonNull Order findOne(@NonNull Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }
}
