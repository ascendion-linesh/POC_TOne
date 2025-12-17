package com.bookstore.domain;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ShoppingCart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal grandTotal;
    
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CartItem> cartItemList;
    
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    // Constructors
    public ShoppingCart() {}

    public ShoppingCart(BigDecimal grandTotal, List<CartItem> cartItemList, User user) {
        this.grandTotal = grandTotal;
        this.cartItemList = cartItemList;
        this.user = user;
    }

    // Getters and setters using Java 14+ records pattern matching
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Java 14+ toString() method
    @Override
    public String toString() {
        return "ShoppingCart[" +
               "id=" + id + ", " +
               "grandTotal=" + grandTotal + ", " +
               "cartItemList=" + cartItemList + ", " +
               "user=" + user + ']';
    }

    // Java 16+ equals() method
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ShoppingCart) obj;
        return this.id.equals(that.id) &&
               this.grandTotal.equals(that.grandTotal) &&
               this.cartItemList.equals(that.cartItemList) &&
               this.user.equals(that.user);
    }

    // Java 16+ hashCode() method
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, grandTotal, cartItemList, user);
    }
}