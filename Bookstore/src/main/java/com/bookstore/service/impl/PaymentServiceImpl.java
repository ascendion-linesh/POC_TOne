package com.bookstore.service.impl;

import com.bookstore.domain.Payment;
import com.bookstore.domain.UserPayment;
import com.bookstore.service.PaymentService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    @NonNull
    public Payment setByUserPayment(@NonNull UserPayment userPayment, @NonNull Payment payment) {
        payment.setType(userPayment.getType());
        payment.setHolderName(userPayment.getHolderName());
        payment.setCardNumber(userPayment.getCardNumber());
        payment.setExpiryMonth(userPayment.getExpiryMonth());
        payment.setExpiryYear(userPayment.getExpiryYear());
        payment.setCvc(userPayment.getCvc());

        return payment;
    }
}
