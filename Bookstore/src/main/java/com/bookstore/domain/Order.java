package com.bookstore.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.bookstore.domain.security.PasswordResetToken;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime orderDate;
    private LocalDateTime shippingDate;
    private String shippingMethod;
    private String orderStatus;
    private BigDecimal orderTotal;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItemList;

    @OneToOne(cascade = CascadeType.ALL)
    private ShippingAddress shippingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private BillingAddress billingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private Payment payment;

    @ManyToOne
    private User user;

    // Getters and setters using record-like syntax

    public Long id() {
        return id;
    }

    public Order setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDateTime orderDate() {
        return orderDate;
    }

    public Order setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public LocalDateTime shippingDate() {
        return shippingDate;
    }

    public Order setShippingDate(LocalDateTime shippingDate) {
        this.shippingDate = shippingDate;
        return this;
    }

    public String shippingMethod() {
        return shippingMethod;
    }

    public Order setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
        return this;
    }

    public String orderStatus() {
        return orderStatus;
    }

    public Order setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public BigDecimal orderTotal() {
        return orderTotal;
    }

    public Order setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
        return this;
    }

    public List<CartItem> cartItemList() {
        return cartItemList;
    }

    public Order setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        return this;
    }

    public ShippingAddress shippingAddress() {
        return shippingAddress;
    }

    public Order setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }

    public BillingAddress billingAddress() {
        return billingAddress;
    }

    public Order setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
        return this;
    }

    public Payment payment() {
        return payment;
    }

    public Order setPayment(Payment payment) {
        this.payment = payment;
        return this;
    }

    public User user() {
        return user;
    }

    public Order setUser(User user) {
        this.user = user;
        return this;
    }

    public PasswordResetToken getUser() {
        return null;
    }

    public Object getCartItemList() {
        return null;
    }

    public String getId() {
        return "";
    }
}