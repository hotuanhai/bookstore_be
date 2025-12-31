package com.example.demo.controller;

import com.example.demo.dto.StockInRequestDTO;
import com.example.demo.dto.StockOutRequestDTO;
import com.example.demo.dto.StockTransactionDto;
import com.example.demo.entity.user.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock-transactions")
@RequiredArgsConstructor
public class StockTransactionController {
    private final StockService stockService;

    @PostMapping("/stock-in")
    public ResponseEntity<ApiResponse<Void>> addStock(
            @RequestBody @Valid StockInRequestDTO request,
            @AuthenticationPrincipal User user) {

        stockService.addStock(
                request.getEditionId(),
                request.getQuantity(),
                request.getReason(),
                request.getReferenceNumber(),
                request.getNotes(),
                user
        );

        return ResponseEntity.ok(ApiResponse.success(null, "Stock added successfully"));
    }

    @PostMapping("/stock-out")
    public ResponseEntity<ApiResponse<Void>> removeStock(
            @RequestBody @Valid StockOutRequestDTO request,
            @AuthenticationPrincipal User user) {

        stockService.removeStock(
                request.getEditionId(),
                request.getQuantity(),
                request.getReason(),
                request.getReferenceNumber(),
                request.getNotes(),
                user
        );

        return ResponseEntity.ok(ApiResponse.success(null, "Stock removed successfully"));
    }

    @GetMapping("/edition/{editionId}/history")
    public ResponseEntity<ApiResponse<List<StockTransactionDto>>> getStockHistory(
            @PathVariable Long editionId) {

        List<StockTransactionDto> transactions = stockService.getStockHistory(editionId);
        return ResponseEntity.ok(ApiResponse.success(transactions, "Stock history retrieved successfully"));
    }

    @GetMapping("/edition/{editionId}/summary")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getStockSummary(
            @PathVariable Long editionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        int totalIn;
        int totalOut;

        if (startDate != null && endDate != null) {
            totalIn = stockService.getTotalStockIn(editionId, startDate, endDate);
            totalOut = stockService.getTotalStockOut(editionId, startDate, endDate);
        } else {
            totalIn = stockService.getTotalStockIn(editionId);
            totalOut = stockService.getTotalStockOut(editionId);
        }

        int currentBalance = totalIn - totalOut;

        Map<String, Integer> summary = Map.of(
                "totalStockIn", totalIn,
                "totalStockOut", totalOut,
                "currentBalance", currentBalance
        );

        return ResponseEntity.ok(ApiResponse.success(summary, "Stock summary retrieved successfully"));
    }

    @GetMapping("/edition/{editionId}/total-in")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getTotalStockIn(
            @PathVariable Long editionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        int totalIn;
        if (startDate != null && endDate != null) {
            totalIn = stockService.getTotalStockIn(editionId, startDate, endDate);
        } else {
            totalIn = stockService.getTotalStockIn(editionId);
        }

        return ResponseEntity.ok(ApiResponse.success(
                Map.of("totalStockIn", totalIn),
                "Total stock in retrieved successfully"
        ));
    }

    @GetMapping("/edition/{editionId}/total-out")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getTotalStockOut(
            @PathVariable Long editionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        int totalOut;
        if (startDate != null && endDate != null) {
            totalOut = stockService.getTotalStockOut(editionId, startDate, endDate);
        } else {
            totalOut = stockService.getTotalStockOut(editionId);
        }

        return ResponseEntity.ok(ApiResponse.success(
                Map.of("totalStockOut", totalOut),
                "Total stock out retrieved successfully"
        ));
    }

    @GetMapping("/book/{bookId}/summary")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getStockSummaryByBook(
            @PathVariable Long bookId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        int totalIn;
        int totalOut;

        if (startDate != null && endDate != null) {
            totalIn = stockService.getTotalStockInByBook(bookId, startDate, endDate);
            totalOut = stockService.getTotalStockOutByBook(bookId, startDate, endDate);
        } else {
            totalIn = stockService.getTotalStockInByBook(bookId);
            totalOut = stockService.getTotalStockOutByBook(bookId);
        }

        int currentBalance = totalIn - totalOut;

        Map<String, Integer> summary = Map.of(
                "totalStockIn", totalIn,
                "totalStockOut", totalOut,
                "currentBalance", currentBalance
        );

        return ResponseEntity.ok(ApiResponse.success(summary, "Stock summary for book retrieved successfully"));
    }

    @GetMapping("/summary/all")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getAllStockSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        int totalIn;
        int totalOut;

        if (startDate != null && endDate != null) {
            totalIn = stockService.getTotalStockInAll(startDate, endDate);
            totalOut = stockService.getTotalStockOutAll(startDate, endDate);
        } else {
            totalIn = stockService.getTotalStockInAll();
            totalOut = stockService.getTotalStockOutAll();
        }

        Map<String, Integer> summary = Map.of(
                "totalStockIn", totalIn,
                "totalStockOut", totalOut
        );

        return ResponseEntity.ok(ApiResponse.success(summary, "Overall stock summary retrieved successfully"));
    }
}