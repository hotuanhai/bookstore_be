package com.example.demo.controller;

import com.example.demo.dto.WishlistDto;
import com.example.demo.entity.WishlistItem;
import com.example.demo.entity.user.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.WishlistItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistItemController {

    private final WishlistItemService wishlistItemService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistDto>>> getWishList(
            @AuthenticationPrincipal User user) {
        List<WishlistDto> wishList = wishlistItemService.getUserWishlist(user.getId());
        return ResponseEntity.ok(ApiResponse.success(wishList, "Wishlist retrieved successfully"));
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<WishlistDto>>> getWishList(
            @AuthenticationPrincipal User user,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<WishlistDto> wishList =
                wishlistItemService.getUserWishListPaginated(user.getId(), pageable);

        return ResponseEntity.ok(
                ApiResponse.success(wishList, "Wishlist retrieved successfully")
        );
    }

//    @GetMapping("/paginated")
//    public ResponseEntity<ApiResponse<Page<WishlistDto>>> getWishListPaginated(
//            @AuthenticationPrincipal User user,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        Page<WishlistDto> wishList = wishlistItemService.getUserWishListPaginated(
//                user.getId(), PageRequest.of(page, size));
//        return ResponseEntity.ok(ApiResponse.success(wishList, "Wishlist retrieved successfully"));
//    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<WishlistDto>> addToWishList(
            @AuthenticationPrincipal User user,
            @RequestParam Long editionId) {
        WishlistDto item = wishlistItemService.addToWishList(user.getId(), editionId);
        return ResponseEntity.ok(ApiResponse.success(item, "Item added to wishlist"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishList(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId) {
        wishlistItemService.removeFromWishList(user.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success(null, "Item removed from wishlist"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearWishList(
            @AuthenticationPrincipal User user) {
        wishlistItemService.clearWishList(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Wishlist cleared"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getWishListCount(
            @AuthenticationPrincipal User user) {
        long count = wishlistItemService.getWishListCount(user.getId());
        return ResponseEntity.ok(ApiResponse.success(count, "Wishlist count retrieved"));
    }

    @GetMapping("/contains/{editionId}")
    public ResponseEntity<ApiResponse<Boolean>> isInWishList(
            @AuthenticationPrincipal User user,
            @PathVariable Long editionId) {
        boolean exists = wishlistItemService.isInWishList(user.getId(), editionId);
        return ResponseEntity.ok(ApiResponse.success(exists, "Wishlist checked"));
    }

    @GetMapping("/edition-ids")
    public ResponseEntity<ApiResponse<List<Long>>> getWishListEditionIds(
            @AuthenticationPrincipal User user) {
        List<Long> editionIds = wishlistItemService.getUserWishListEditionIds(user.getId());

        return ResponseEntity.ok(
                ApiResponse.success(editionIds, "Wishlist edition IDs retrieved successfully")
        );
    }

}
