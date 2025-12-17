package com.bookstore.repository;

import com.bookstore.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @NonNull
    User findByUsername(String username);

    @NonNull
    User findByEmail(String email);

    @Override
    @NonNull
    Iterable<User> findAll();

    @Override
    @NonNull
    Optional<User> findById(@NonNull Long id);

    @Override
    <S extends User> @NonNull S save(@NonNull S entity);

    @Override
    void deleteById(@NonNull Long id);
}
