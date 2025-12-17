package com.bookstore.service.impl;

import com.bookstore.domain.*;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.repository.*;
import com.bookstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPaymentRepository userPaymentRepository;
    private final UserShippingRepository userShippingRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserServiceImpl(@NonNull UserRepository userRepository,
                           @NonNull RoleRepository roleRepository,
                           @NonNull UserPaymentRepository userPaymentRepository,
                           @NonNull UserShippingRepository userShippingRepository,
                           @NonNull PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userPaymentRepository = userPaymentRepository;
        this.userShippingRepository = userShippingRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public PasswordResetToken getPasswordResetToken(@NonNull final String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void createPasswordResetTokenForUser(@NonNull final User user, @NonNull final String token) {
        // Check if a token already exists for this user
        PasswordResetToken existingToken = passwordResetTokenRepository.findByUserId(user.getId());

        if (existingToken != null) {
            // If a token exists, you can either update or delete the old one and insert a new one
            // Example: Update the existing token
            existingToken.setToken(token);
            passwordResetTokenRepository.save(existingToken);
        } else {
            // If no token exists, create a new one
            PasswordResetToken myToken = new PasswordResetToken(token, user);
            passwordResetTokenRepository.save(myToken);
        }
    }


    @Override
    public User findByUsername(@NonNull String username) {
        return (User) userRepository.findByUsername(username);
    }

    @Override
    public User findById(@NonNull Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User createUser(@NonNull User user, @NonNull Set<UserRole> userRoles) {
        User localUser = userRepository.findByUsername(user.getUsername());

        if (localUser != null) {
            LOG.info("User {} already exists. Nothing will be done.", user.getUsername());
        } else {
            for (UserRole ur : userRoles) {
                Role existingRole = roleRepository.findByName(ur.getRole().getName())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + ur.getRole().getName()));
                ur.setRole(existingRole); // replace transient role with persistent one
            }

            user.getUserRoles().addAll(userRoles);

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(user);
            user.setShoppingCart(shoppingCart);

            user.setUserShippingList(new ArrayList<>());
            user.setUserPaymentList(new ArrayList<>());

            localUser = userRepository.save(user);
        }

        return localUser;
    }

    @Override
    @Transactional
    public User save(@NonNull User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserBilling(@NonNull UserBilling userBilling, @NonNull UserPayment userPayment, @NonNull User user) {
        userPayment.setUser(user);
        userPayment.setUserBilling(userBilling);
        userPayment.setDefaultPayment(true);
        userBilling.setUserPayment(userPayment);
        user.getUserPaymentList().add(userPayment);
        save(user);
    }

    @Override
    @Transactional
    public void updateUserShipping(@NonNull UserShipping userShipping, @NonNull User user) {
        userShipping.setUser(user);
        userShipping.setUserShippingDefault(true);
        user.getUserShippingList().add(userShipping);
        save(user);
    }

    @Override
    @Transactional
    public void setUserDefaultPayment(@NonNull Long userPaymentId, @NonNull User user) {
        List<UserPayment> userPaymentList = userPaymentRepository.findByUser(user);

        for (UserPayment userPayment : userPaymentList) {
            if (userPayment.getId().equals(userPaymentId)) {
                userPayment.setDefaultPayment(true);
            } else {
                userPayment.setDefaultPayment(false);
            }
            userPaymentRepository.save(userPayment);
        }
    }

    @Override
    @Transactional
    public void setUserDefaultShipping(@NonNull Long userShippingId, @NonNull User user) {
        List<UserShipping> userShippingList = userShippingRepository.findByUser(user);

        for (UserShipping userShipping : userShippingList) {
            if (userShipping.getId().equals(userShippingId)) {
                userShipping.setUserShippingDefault(true);
            } else {
                userShipping.setUserShippingDefault(false);
            }
            userShippingRepository.save(userShipping);
        }
    }
}
