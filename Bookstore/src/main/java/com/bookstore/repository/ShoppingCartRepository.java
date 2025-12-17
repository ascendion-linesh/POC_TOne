package com.bookstore.repository;

import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.security.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends CrudRepository<Role, Long> {

    @NonNull
    Optional<Role> findByName(@NonNull String name);

    @Override
    @NonNull
    Iterable<Role> findAll();

    @Override
    @NonNull
    Optional<Role> findById(@NonNull Long id);

    @Override
    <S extends Role> @NonNull S save(@NonNull S entity);

    @Override
    void deleteById(@NonNull Long id);

    void save(ShoppingCart shoppingCart);
}
