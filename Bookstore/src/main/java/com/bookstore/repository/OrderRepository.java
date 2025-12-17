package com.bookstore.repository;

import com.bookstore.domain.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    
    @Override
    @NonNull
    Iterable<Order> findAll();

    @Override
    @NonNull
    java.util.Optional<Order> findById(@NonNull Long id);

    @Override
    <S extends Order> @NonNull S save(@NonNull S entity);

    @Override
    void deleteById(@NonNull Long id);
}
