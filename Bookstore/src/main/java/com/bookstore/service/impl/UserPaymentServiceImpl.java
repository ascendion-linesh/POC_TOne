package com.bookstore.service.impl;

import com.bookstore.domain.UserPayment;
import com.bookstore.repository.UserPaymentRepository;
import com.bookstore.service.UserPaymentService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserPaymentServiceImpl implements UserPaymentService {

    private final UserPaymentRepository userPaymentRepository;

    public UserPaymentServiceImpl(@NonNull UserPaymentRepository userPaymentRepository) {
        this.userPaymentRepository = userPaymentRepository;
    }

    @Override
    @NonNull
    public UserPayment findById(@NonNull Long id) {
        return userPaymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserPayment not found with id: " + id));
    }

    @Override
    public void removeById(@NonNull Long id) {
        userPaymentRepository.deleteById(id);
    }
}
