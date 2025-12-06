package com.example.demo.controller;

import com.example.demo.dto.CartDto;
import com.example.demo.entity.user.User;
import com.example.demo.request.AddToCartRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(@AuthenticationPrincipal User user) {
        CartDto cart = cartService.getCart(user.getId());
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart retrieved successfully"));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddToCartRequest request) {
        CartDto cart = cartService.addToCart(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(cart, "Item added to cart"));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId,
            int quantity) {
        CartDto cart = cartService.updateCartItem(user.getId(), cartItemId, quantity);
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart item updated"));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartDto>> removeFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId) {
        CartDto cart = cartService.removeFromCart(user.getId(), cartItemId);
        return ResponseEntity.ok(ApiResponse.success(cart, "Item removed from cart"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared"));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Void>> validateCart(@AuthenticationPrincipal User user) {
        cartService.validateCart(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Cart is valid"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartItemsCount(
            @AuthenticationPrincipal User user) {
        int count = cartService.getCartQuantity(user.getId());
        return ResponseEntity.ok(ApiResponse.success(count, "Cart items count retrieved"));
    }

    @PostMapping("/cleanup")
    public ResponseEntity<ApiResponse<CartDto>> removeUnavailableItems(
            @AuthenticationPrincipal User user) {
        CartDto cart = cartService.removeUnavailableItems(user.getId());
        return ResponseEntity.ok(ApiResponse.success(cart, "Unavailable items removed"));
    }
}
