package com.bookstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
		name = "cart_item",
		uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "shopping_cart_id"})
)
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int qty;

	private BigDecimal subtotal;

	@OneToOne
	private Book book;

	@OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<BookToCartItem> bookToCartItemList;

	@ManyToOne
	@JoinColumn(name = "shopping_cart_id")
	private ShoppingCart shoppingCart;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	// --- Getters and Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public List<BookToCartItem> getBookToCartItemList() {
		return bookToCartItemList;
	}

	public void setBookToCartItemList(List<BookToCartItem> bookToCartItemList) {
		this.bookToCartItemList = bookToCartItemList;
	}

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	// --- Optional: Equals and HashCode using Java 21's new record-like conciseness ---

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CartItem other)) return false;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	// --- Optional: toString() for debugging ---
	@Override
	public String toString() {
		return "CartItem{" +
				"id=" + id +
				", qty=" + qty +
				", subtotal=" + subtotal +
				", book=" + (book != null ? book.getId() : null) +
				", shoppingCart=" + (shoppingCart != null ? shoppingCart.getId() : null) +
				", order=" + (order != null ? order.getId() : null) +
				'}';
	}
}
