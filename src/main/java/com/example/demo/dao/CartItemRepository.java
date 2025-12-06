package com.example.demo.dao;

import com.example.demo.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndEditionId(Long cartId, Long editionId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.edition.id = :editionId")
    Optional<CartItem> findByCartAndEdition(Long cartId, Long editionId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(Long cartId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.user.id = :userId")
    int countByUserId(Long userId);

    List<CartItem> findByCartId(Long cartId);

    boolean existsByCartIdAndEditionId(Long cartId, Long editionId);

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.cart.user.id = :userId")
    int sumQuantityByUserId(@Param("userId") Long userId);
}
