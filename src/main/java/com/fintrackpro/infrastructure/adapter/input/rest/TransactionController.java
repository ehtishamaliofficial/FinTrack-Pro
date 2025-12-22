package com.fintrackpro.infrastructure.adapter.input.rest;

import com.fintrackpro.application.port.input.CurrentUserProvider;
import com.fintrackpro.application.port.input.TransactionUseCase;
import com.fintrackpro.domain.model.Transaction;
import com.fintrackpro.domain.model.TransactionFilter;
import com.fintrackpro.infrastructure.adapter.input.dto.request.CreateTransactionRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.UpdateTransactionRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.response.ApiResponse;
import com.fintrackpro.infrastructure.adapter.input.mapper.TransactionApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.fintrackpro.domain.valueobject.TransactionType;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction Management", description = "APIs for recording and managing financial transactions")
public class TransactionController {

    private final TransactionUseCase transactionUseCase;
    private final TransactionApiMapper transactionMapper;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "Create a new transaction")
    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        log.info("Creating new {} transaction for user", request.type());
        Transaction transaction = transactionMapper.toModel(request, currentUserProvider);
        Transaction created = transactionUseCase.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully", created));
    }

    @Operation(summary = "Get transaction by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(@PathVariable Long id) {
        return transactionUseCase.getTransactionById(id)
                .map(t -> ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", t)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Transaction not found")));
    }

    @Operation(summary = "Get all transactions with filtering and pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Transaction>>> getAllTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long walletId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate,desc") String sort) {

        Long userId = currentUserProvider.getCurrentUserId();

        TransactionFilter filter = new TransactionFilter(
                startDate, endDate, categoryId, walletId, type, minAmount, maxAmount);

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction sortDirection = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        Page<Transaction> transactions = transactionUseCase.getFilteredTransactions(userId, filter, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @Operation(summary = "Get transactions by wallet")
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionsByWallet(@PathVariable Long walletId) {
        Long userId = currentUserProvider.getCurrentUserId();
        List<Transaction> transactions = transactionUseCase.getWalletTransactions(userId, walletId);
        return ResponseEntity.ok(ApiResponse.success("Wallet transactions retrieved successfully", transactions));
    }

    @Operation(summary = "Filter transactions by date range")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Transaction>>> filterTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = currentUserProvider.getCurrentUserId();
        List<Transaction> transactions = transactionUseCase.getTransactionsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Filtered transactions retrieved successfully", transactions));
    }

    @Operation(summary = "Search transactions")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Transaction>>> searchTransactions(@RequestParam String query) {
        Long userId = currentUserProvider.getCurrentUserId();
        List<Transaction> transactions = transactionUseCase.searchTransactions(userId, query);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", transactions));
    }

    @Operation(summary = "Update an existing transaction")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        log.info("Updating transaction with id: {}", id);

        Transaction existing = transactionUseCase.getTransactionById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Use mapper to apply updates to a new transaction object
        // The mapping is used to validate and transform the request, though we
        // reconstruct the object below
        @SuppressWarnings("unused")
        Transaction updateData = transactionMapper.toModel(request, currentUserProvider);

        // Reconstruct with ID from path and other fixed fields
        Transaction updated = new Transaction(
                id,
                existing.userId(),
                request.walletId() != null ? request.walletId() : existing.walletId(),
                request.categoryId() != null ? request.categoryId() : existing.categoryId(),
                request.toWalletId() != null ? request.toWalletId() : existing.toWalletId(),
                existing.type(),
                request.amount(),
                existing.currency(),
                request.transactionDate() != null ? request.transactionDate() : existing.transactionDate(),
                request.description() != null ? request.description() : existing.description(),
                request.notes() != null ? request.notes() : existing.notes(),
                existing.status(),
                request.referenceNumber() != null ? request.referenceNumber() : existing.referenceNumber(),
                request.payee() != null ? request.payee() : existing.payee(),
                request.location() != null ? request.location() : existing.location(),
                request.tags() != null ? request.tags() : existing.tags(),
                request.receiptUrl() != null ? request.receiptUrl() : existing.receiptUrl(),
                request.attachmentUrl() != null ? request.attachmentUrl() : existing.attachmentUrl(),
                existing.isRecurring(),
                existing.recurringPattern(),
                existing.recurringGroupId(),
                existing.createdAt(),
                java.time.LocalDateTime.now(),
                existing.createdBy(),
                currentUserProvider.getCurrentUserId(),
                null,
                false);

        Transaction saved = transactionUseCase.updateTransaction(updated);
        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", saved));
    }

    @Operation(summary = "Delete a transaction")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        log.info("Deleting transaction with id: {}", id);
        transactionUseCase.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }
}
