package com.example.demo.service;

import com.example.demo.dao.BookEditionRepository;
import com.example.demo.dao.OrderItemRepository;
import com.example.demo.dao.OrderRepository;
import com.example.demo.dao.StockTransactionRepository;
import com.example.demo.dto.StockTransactionDto;
import com.example.demo.entity.StockTransaction;
import com.example.demo.entity.book.BookEdition;
import com.example.demo.entity.order.Order;
import com.example.demo.entity.order.OrderItem;
import com.example.demo.entity.user.User;
import com.example.demo.enums.StockReason;
import com.example.demo.enums.TransactionType;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final BookEditionRepository editionRepository;
    private final StockTransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void addStock(Long editionId, int quantity, StockReason reason,
                         String referenceNumber, String notes, User user) {
        BookEdition edition = editionRepository.findById(editionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edition not found"));

        int stockBefore = edition.getStock();
        int stockAfter = stockBefore + quantity;

        StockTransaction transaction = StockTransaction.builder()
                .bookEdition(edition)
                .type(TransactionType.IN)
                .quantity(quantity)
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .reason(reason)
                .referenceNumber(referenceNumber)
                .notes(notes)
                .createdBy(user)
                .build();

        edition.setStock(stockAfter);

        transactionRepository.save(transaction);
        editionRepository.save(edition);
    }

    @Transactional
    public void removeStock(Long editionId, int quantity, StockReason reason,
                            String referenceNumber, String notes, User user) {
        BookEdition edition = editionRepository.findById(editionId)
                .orElseThrow(() -> new ResourceNotFoundException("Edition not found"));

        if (edition.getStock() < quantity) {
            throw new InsufficientStockException("Not enough stock available");
        }

        int stockBefore = edition.getStock();
        int stockAfter = stockBefore - quantity;

        StockTransaction transaction = StockTransaction.builder()
                .bookEdition(edition)
                .type(TransactionType.OUT)
                .quantity(quantity)
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .reason(reason)
                .referenceNumber(referenceNumber)
                .notes(notes)
                .createdBy(user)
                .build();

        edition.setStock(stockAfter);

        transactionRepository.save(transaction);
        editionRepository.save(edition);
    }

    @Transactional(readOnly = true)
    public List<StockTransactionDto> getStockHistory(Long editionId) {
        List<StockTransaction> list =
                transactionRepository.findByBookEditionIdOrderByTransactionDateDesc(editionId);
        return list.stream().map(this::toDto).toList();
    }

    // for edition
    @Transactional(readOnly = true)
    public int getTotalStockIn(Long editionId) {
        Integer total = transactionRepository.getTotalQuantityByEditionAndType(editionId, TransactionType.IN);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockOut(Long editionId) {
        Integer total = transactionRepository.getTotalQuantityByEditionAndType(editionId, TransactionType.OUT);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockIn(Long editionId, LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = transactionRepository.getTotalQuantityByEditionTypeAndDateRange(
                editionId, TransactionType.IN, startDate, endDate);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockOut(Long editionId, LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = transactionRepository.getTotalQuantityByEditionTypeAndDateRange(
                editionId, TransactionType.OUT, startDate, endDate);
        return total != null ? total : 0;
    }

    // fro book
    @Transactional(readOnly = true)
    public int getTotalStockInByBook(Long bookId) {
        Integer total = transactionRepository.getTotalQuantityByBookAndType(bookId, TransactionType.IN);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockOutByBook(Long bookId) {
        Integer total = transactionRepository.getTotalQuantityByBookAndType(bookId, TransactionType.OUT);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockInByBook(Long bookId, LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = transactionRepository.getTotalQuantityByBookTypeAndDateRange(
                bookId, TransactionType.IN, startDate, endDate);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockOutByBook(Long bookId, LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = transactionRepository.getTotalQuantityByBookTypeAndDateRange(
                bookId, TransactionType.OUT, startDate, endDate);
        return total != null ? total : 0;
    }

    //for all
    @Transactional(readOnly = true)
    public int getTotalStockInAll() {
        Integer total = transactionRepository.getTotalQuantityByType(TransactionType.IN);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockOutAll() {
        Integer total = transactionRepository.getTotalQuantityByType(TransactionType.OUT);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockInAll(LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = transactionRepository.getTotalQuantityByTypeAndDateRange(
                TransactionType.IN, startDate, endDate);
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalStockOutAll(LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = transactionRepository.getTotalQuantityByTypeAndDateRange(
                TransactionType.OUT, startDate, endDate);
        return total != null ? total : 0;
    }

    @Transactional
    public void removeStockForOrder(Long orderId, String paymentIntentId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Order not found: " + orderId
                    ));
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

            User systemUser = order.getUser();

            // Remove stock for each item
            for (OrderItem item : orderItems) {
                if (item.getEdition() != null) {
                    removeStock(
                            item.getEdition().getId(),
                            item.getQuantity(),
                            StockReason.SALE,
                            paymentIntentId, // Use payment intent as reference
                            String.format("Stock removed from order #%d - Payment: %s",
                                    orderId, paymentIntentId),
                            systemUser
                    );

                    log.info("Removed stock for edition {}: {} units from order {}",
                            item.getEdition().getId(),
                            item.getQuantity(),
                            orderId);
                }
            }
        } catch (Exception e) {
            log.error("Failed to remove stock for order: {}", orderId, e);
            throw new RuntimeException("Failed to remove stock after payment", e);
        }
    }

    //helper method
    private StockTransactionDto toDto(StockTransaction tx) {
        return StockTransactionDto.builder()
                .id(tx.getId())
                .bookEditionId(tx.getBookEdition().getId())
                .productName(
                        tx.getBookEdition().getBook().getTitle()
                                + " - "
                                + tx.getBookEdition().getName()
                )
                .type(tx.getType())
                .quantity(tx.getQuantity())
                .stockBefore(tx.getStockBefore())
                .stockAfter(tx.getStockAfter())
                .reason(tx.getReason())
                .referenceNumber(tx.getReferenceNumber())
                .notes(tx.getNotes())
                .createdBy(
                        tx.getCreatedBy() != null
                                ? tx.getCreatedBy().getUsername()
                                : null
                )
                .transactionDate(tx.getTransactionDate())
                .build();
    }

}
