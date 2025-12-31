package com.example.demo.dto;

import com.example.demo.enums.StockReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockInRequestDTO {
    @NotNull(message = "Edition ID is required")
    private Long editionId;

    @Positive(message = "Quantity must be positive")
    private int quantity;

    @NotNull(message = "Reason is required")
    private StockReason reason;

    private String referenceNumber;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
