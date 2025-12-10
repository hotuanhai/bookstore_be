package com.example.demo.service;

import com.example.demo.dao.BookEditionRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dao.WishlistItemRepository;
import com.example.demo.dto.WishlistDto;
import com.example.demo.entity.WishlistItem;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.user.User;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistItemService {
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final BookEditionRepository bookEditionRepository;

    @Transactional
    public WishlistDto addToWishList(Long userId, Long editionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BookEdition edition = bookEditionRepository.findById(editionId)
                .orElseThrow(() -> new ResourceNotFoundException("Book edition not found"));

        // Check if already exists
        if (wishlistItemRepository.existsByUserIdAndBookEditionId(userId, editionId)) {
            throw new DuplicateResourceException("Book already in wishlist");
        }

        WishlistItem item = WishlistItem.builder()
                .user(user)
                .bookEdition(edition)
                .build();

        WishlistItem savedItem = wishlistItemRepository.save(item);

        return WishlistDto.builder()
                .wishlistItemId(savedItem.getId())
                .bookEditionId(savedItem.getBookEdition().getId())
                .createdAt(savedItem.getCreatedAt())
                .build();
    }

    @Transactional
    public void removeFromWishList(Long userId, Long wishListItemId) {
        WishlistItem item = wishlistItemRepository.findById(wishListItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        if (!item.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Cannot remove another user's wishlist item");
        }

        wishlistItemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public List<WishlistDto> getUserWishlist(Long userId) {
        List<WishlistItem> items = wishlistItemRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return items.stream()
                .map(item -> WishlistDto.builder()
                        .wishlistItemId(item.getId())
                        .bookEditionId(item.getBookEdition().getId())
                        .createdAt(item.getCreatedAt())
                        .build())
                .toList();
    }

//    @Transactional(readOnly = true)
//    public Page<WishlistDto> getUserWishListPaginated(Long userId, Pageable pageable) {
//        Page<WishlistItem> items = wishlistItemRepository.findByUserId(userId,
//                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
//                        Sort.by(Sort.Direction.DESC, "createdAt")));
//
//        return items.map(item -> WishlistDto.builder()
//                .wishlistItemId(item.getId())
//                .bookEditionId(item.getBookEdition().getId())
//                .createdAt(item.getCreatedAt())
//                .build());
//    }

    @Transactional
    public void clearWishList(Long userId) {
        wishlistItemRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getWishListCount(Long userId) {
        return wishlistItemRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isInWishList(Long userId, Long editionId) {
        return wishlistItemRepository.existsByUserIdAndBookEditionId(userId, editionId);
    }

    @Transactional(readOnly = true)
    public List<Long> getUserWishListEditionIds(Long userId) {
        return wishlistItemRepository.findEditionIdsByUserId(userId);
    }
}