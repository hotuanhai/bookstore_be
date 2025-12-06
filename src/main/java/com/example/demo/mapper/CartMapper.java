package com.example.demo.mapper;

import com.example.demo.dto.CartDto;
import com.example.demo.dto.CartItemDto;
import com.example.demo.entity.book.Book;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.cart.Cart;
import com.example.demo.entity.cart.CartItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    public CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(!cart.getCartItems().isEmpty() ?
                        cart.getCartItems().stream()
                                .map(this::toCartItemDto)
                                .collect(Collectors.toList()) :
                        new ArrayList<>())
                .totalPrice(cart.getTotalPrice())
                .totalItems(cart.getTotalItems())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    public CartItemDto toCartItemDto(CartItem item) {
        if (item == null || item.getEdition() == null) {
            return null;
        }

        BookEdition edition = item.getEdition();
        Book book = edition.getBook();

        return CartItemDto.builder()
                .id(item.getId())
                .editionId(edition.getId())
                .bookTitle(book != null ? book.getTitle() : "Unknown")
                .editionName(edition.getName())
                .imageUrl(edition.getImageUrl())
                .quantity(item.getQuantity())
                .currentPrice(item.getCurrentPrice())
                .originalPrice(item.getCurrentPrice())
                .subTotal(item.getSubtotal())
                .availableStock(edition.getStock())
                .status(edition.getStatus())
                .addedAt(item.getAddedAt())
                .build();
    }
}
