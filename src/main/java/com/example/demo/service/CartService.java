package com.example.demo.service;

import com.example.demo.dao.*;
import com.example.demo.dto.CartDto;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.cart.Cart;
import com.example.demo.entity.cart.CartItem;
import com.example.demo.entity.user.User;
import com.example.demo.enums.BookStatus;
import com.example.demo.exception.*;
import com.example.demo.mapper.CartMapper;
import com.example.demo.request.AddToCartRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookEditionRepository bookEditionRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("User not found"));
                    Cart cart = Cart.builder()
                            .user(user)
                            .cartItems(new ArrayList<>())
                            .build();
                    return cartRepository.save(cart);
                });
    }

    @Transactional(readOnly = true)
    public CartDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> getOrCreateCart(userId));
        // Auto-cleanup invalid items
        boolean hasChanges = autoCleanupCart(cart);

        if (hasChanges) {
            cart = cartRepository.save(cart);
        }

        return cartMapper.toDto(cart);
    }

    @Transactional
    public CartDto addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        BookEdition edition = bookEditionRepository.findById(request.getEditionId())
                .orElseThrow(() -> new BookNotFoundException("Book edition not found"));

        // Check if edition is available
        if (edition.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("This edition is not available for purchase");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndEditionId(cart.getId(), edition.getId());

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            // Check stock availability
            if (edition.getStock() < newQuantity) {
                throw new InsufficientStockException(
                        "Only " + edition.getStock() + " items available in stock"
                );
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Check stock availability
            if (edition.getStock() < request.getQuantity()) {
                throw new InsufficientStockException(
                        "Only " + edition.getStock() + " items available in stock"
                );
            }

            // Add new item
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .edition(edition)
                    .quantity(request.getQuantity())
                    .build();

            cart.addItem(cartItem);
            cartItemRepository.save(cartItem);
        }

        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Transactional
    public CartDto updateCartItem(Long userId, Long cartItemId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Check stock availability
        if (cartItem.getEdition().getStock() < quantity) {
            throw new InsufficientStockException(
                    "Only " + cartItem.getEdition().getStock() + " items available in stock"
            );
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        cart = cartRepository.findByUserIdWithItems(userId).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Transactional
    public CartDto removeFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        cart = cartRepository.findByUserIdWithItems(userId).orElse(cart);
        return cartMapper.toDto(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow();

        cart.clearCart();
        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public int getCartQuantity(Long userId){
//        Optional<Cart> cart = cartRepository.findByUserId(userId);
//        return cart.map(Cart::getTotalItems).orElse(0);
        return cartItemRepository.sumQuantityByUserId(userId);
    }

    // Validate cart before checkout
    @Transactional(readOnly = true)
    public void validateCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        List<String> errors = new ArrayList<>();

        for (CartItem item : cart.getCartItems()) {
            BookEdition edition = item.getEdition();

            // Check if edition still exists and is available
            if (edition == null) {
                errors.add("Book edition not found in cart");
                continue;
            }

            // Check if edition is available
            if (edition.getStatus() != BookStatus.AVAILABLE) {
                errors.add(edition.getName() + " is no longer available");
            }

            // Check stock
            if (edition.getStock() < item.getQuantity()) {
                errors.add(String.format(
                        "Insufficient stock for %s - %s. Available: %d, Requested: %d",
                        edition.getBook().getTitle(),
                        edition.getName(),
                        edition.getStock(),
                        item.getQuantity()
                ));
            }
        }

        if (!errors.isEmpty()) {
            throw new CartValidationException(String.join("; ", errors));
        }
    }

    @Transactional
    public CartDto removeUnavailableItems(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartItem> itemsToRemove = cart.getCartItems().stream()
                .filter(item -> item.getEdition().getStatus() != BookStatus.AVAILABLE ||
                        item.getEdition().getStock() < item.getQuantity())
                .toList();

        itemsToRemove.forEach(item -> {
            cart.removeItem(item);
            cartItemRepository.delete(item);
        });

        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    // helper
    /**
     * Auto-cleanup cart items that are invalid:
     * - Remove items that are no longer AVAILABLE
     * - Reduce quantity if stock is less than cart quantity
     *
     * @return true if any changes were made
     */
    private boolean autoCleanupCart(Cart cart) {
        boolean hasChanges = false;
        List<CartItem> itemsToRemove = new ArrayList<>();

        for (CartItem item : cart.getCartItems()) {
            BookEdition edition = item.getEdition();

            if (edition == null || edition.getStatus() != BookStatus.AVAILABLE) {
                // Remove unavailable items
                itemsToRemove.add(item);
                hasChanges = true;
            } else if (edition.getStock() < item.getQuantity()) {
                if (edition.getStock() == 0) {
                    // Remove if out of stock
                    itemsToRemove.add(item);
                } else {
                    // Reduce quantity to available stock
                    item.setQuantity(edition.getStock());
                    cartItemRepository.save(item);
                }
                hasChanges = true;
            }
        }

        // Remove items marked for removal
        for (CartItem item : itemsToRemove) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
        }

        return hasChanges;
    }
}
