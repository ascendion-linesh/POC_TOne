package com.bookstore.repository;

import com.bookstore.domain.User;
import com.bookstore.domain.UserPayment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPaymentRepository extends CrudRepository<UserPayment, Long> {

    @Override
    @NonNull
    Iterable<UserPayment> findAll();

    @Override
    @NonNull
    Optional<UserPayment> findById(@NonNull Long id);

    @Override
    <S extends UserPayment> @NonNull S save(@NonNull S entity);

    @Override
    void deleteById(@NonNull Long id);

    List<UserPayment> findByUser(User user);
}
