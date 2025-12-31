package com.example.demo.dto;

import com.example.demo.enums.StockReason;
import com.example.demo.enums.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransactionDto {
    private Long id;
    private Long bookEditionId;
    private String productName;
    private TransactionType type;
    private int quantity;
    private int stockBefore;
    private int stockAfter;
    private StockReason reason;
    private String referenceNumber; // Order ID
    private String notes;
    private String createdBy;
    private LocalDateTime transactionDate;
}
