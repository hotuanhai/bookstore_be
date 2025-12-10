package com.example.demo.dao;

import com.example.demo.entity.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<WishlistItem> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndBookEditionId(Long userId, Long editionId);

    Optional<WishlistItem> findByUserIdAndBookEditionId(Long userId, Long editionId);

    long countByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM WishlistItem w WHERE w.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT w.bookEdition.id FROM WishlistItem w WHERE w.user.id = :userId")
    List<Long> findEditionIdsByUserId(@Param("userId") Long userId);
}
