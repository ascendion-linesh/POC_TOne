package com.bookstore.domain.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.bookstore.domain.User;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "token"}))
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;  // Token expiration time in minutes (24 hours)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Instant expiryDate;

    // Default constructor
    public PasswordResetToken() {}

    // Constructor to create a new token (with manual token input)
    public PasswordResetToken(final String token, final User user) {
        this.token = token;  // Token passed in directly
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);  // Set expiration date
    }

    // Constructor to create a new token with auto-generated token
    public PasswordResetToken(final User user) {
        this.token = generateToken();  // Generate a unique token
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);  // Set expiration date
    }

    // Generate a unique token using UUID
    private String generateToken() {
        return UUID.randomUUID().toString();  // Using UUID for guaranteed uniqueness
    }

    // Calculate the expiration date based on the current time and expiration duration
    private Instant calculateExpiryDate(final int expiryTimeInMinutes) {
        return Instant.now().plus(expiryTimeInMinutes, ChronoUnit.MINUTES);
    }

    // Update token and reset expiration date
    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);  // Reset expiration time
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public static int getExpiration() {
        return EXPIRATION;
    }

    @Override
    public String toString() {
        return "PasswordResetToken [id=" + id + ", token=" + token + ", user=" + user + ", expiryDate=" + expiryDate + "]";
    }
}
