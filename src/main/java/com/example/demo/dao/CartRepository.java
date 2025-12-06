package com.example.demo.dao;

import com.example.demo.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long id);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.edition WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(Long userId);

    boolean existsByUserId(Long userId);
}
