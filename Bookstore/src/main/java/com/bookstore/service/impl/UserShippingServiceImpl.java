package com.bookstore.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bookstore.domain.UserShipping;
import com.bookstore.repository.UserShippingRepository;
import com.bookstore.service.UserShippingService;

@Service
public class UserShippingServiceImpl implements UserShippingService {

    private final UserShippingRepository userShippingRepository;

    public UserShippingServiceImpl(UserShippingRepository userShippingRepository) {
        this.userShippingRepository = userShippingRepository;
    }

    @Override
    public UserShipping findById(Long id) {
        Optional<UserShipping> userShipping = userShippingRepository.findById(id);
        return userShipping.orElse(null);
    }

    @Override
    public void removeById(Long id) {
        userShippingRepository.deleteById(id);
    }
}
