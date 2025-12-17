package com.bookstore.repository;

import com.bookstore.domain.User;
import com.bookstore.domain.UserShipping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserShippingRepository extends CrudRepository<UserShipping, Long> {

    @Override
    @NonNull
    Iterable<UserShipping> findAll();

    @Override
    @NonNull
    Optional<UserShipping> findById(@NonNull Long id);

    @Override
    <S extends UserShipping> @NonNull S save(@NonNull S entity);

    @Override
    void deleteById(@NonNull Long id);

    List<UserShipping> findByUser(User user);
}
