package com.fintrackpro.infrastructure.adapter.input.rest;

import com.fintrackpro.application.port.input.CurrentUserProvider;
import com.fintrackpro.application.port.input.WalletUseCase;
import com.fintrackpro.domain.model.Wallet;
import com.fintrackpro.domain.valueobject.WalletType;
import com.fintrackpro.infrastructure.adapter.input.dto.response.ApiResponse;
import com.fintrackpro.infrastructure.adapter.input.dto.request.CreateWalletRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.TransferRequest;
import com.fintrackpro.infrastructure.adapter.input.dto.request.UpdateWalletRequest;
import com.fintrackpro.infrastructure.adapter.input.mapper.WalletApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallets")
@Tag(name = "Wallet Management", description = "APIs for managing user wallets, balances, and transfers")
public class WalletController {

    private final WalletUseCase walletUseCase;
    private final WalletApiMapper walletApiMapper;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Create a new wallet",
            description = "Creates a new wallet for the authenticated user with the specified details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Wallet created successfully",
                    content = @Content(schema = @Schema(implementation = Wallet.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Wallet>> createWallet(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Wallet creation details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateWalletRequest.class))
            )
            @Valid @RequestBody CreateWalletRequest request) {
        log.info("Creating new wallet for user");
        Wallet wallet = walletApiMapper.toModel(request,currentUserProvider);
        Wallet createdWallet = walletUseCase.createWallet(wallet);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wallet created successfully", createdWallet));
    }

    @Operation(
            summary = "Get wallet by ID",
            description = "Retrieves detailed information about a specific wallet"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Wallet retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Wallet.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Wallet>> getWallet(
            @Parameter(description = "Wallet ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("Fetching wallet with id: {}", id);
        return walletUseCase.getWalletById(id)
                .map(wallet -> ResponseEntity.ok(ApiResponse.success("Wallet retrieved successfully", wallet)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Wallet not found")));
    }

    @Operation(
            summary = "Get all user wallets",
            description = "Retrieves all wallets belonging to the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Wallets retrieved successfully",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Wallet>>> getUserWallets() {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        log.info("Fetching all wallets for user: {}", userId);
        List<Wallet> wallets = walletUseCase.getUserWallets(userId);
        return ResponseEntity.ok(ApiResponse.success("Wallets retrieved successfully", wallets));
    }

    @Operation(
            summary = "Update wallet",
            description = "Updates the details of an existing wallet"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Wallet updated successfully",
                    content = @Content(schema = @Schema(implementation = Wallet.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Wallet>> updateWallet(
            @Parameter(description = "Wallet ID", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated wallet details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateWalletRequest.class))
            )
            @Valid @RequestBody UpdateWalletRequest request) {
        log.info("Updating wallet with id: {}", id);
        Wallet wallet = walletApiMapper.toModel(request).toBuilder().id(id).build();
        Wallet updatedWallet = walletUseCase.updateWallet(wallet);
        return ResponseEntity.ok(ApiResponse.success("Wallet updated successfully", updatedWallet));
    }

    @Operation(
            summary = "Delete wallet",
            description = "Permanently deletes a wallet from the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Wallet deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWallet(
            @Parameter(description = "Wallet ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("Deleting wallet with id: {}", id);
        walletUseCase.deleteWallet(id);
        return ResponseEntity.ok(ApiResponse.success("Wallet deleted successfully", null));
    }

    @Operation(
            summary = "Update wallet balance",
            description = "Adds or subtracts an amount from the wallet's current balance"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Balance updated successfully",
                    content = @Content(schema = @Schema(implementation = Wallet.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<Wallet>> updateWalletBalance(
            @Parameter(description = "Wallet ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Amount to add (positive) or subtract (negative)", required = true, example = "100.50")
            @RequestParam BigDecimal amount) {
        log.info("Updating balance for wallet: {} by amount: {}", id, amount);
        Wallet updatedWallet = walletUseCase.updateWalletBalance(id, amount);
        return ResponseEntity.ok(ApiResponse.success("Wallet balance updated successfully", updatedWallet));
    }

    @Operation(
            summary = "Transfer between wallets",
            description = "Transfers a specified amount from one wallet to another"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully",
                    content = @Content(schema = @Schema(implementation = Wallet[].class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid transfer request or insufficient balance",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Source or target wallet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<Wallet[]>> transferBetweenWallets(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Transfer details including source, target, amount and description",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransferRequest.class))
            )
            @Valid @RequestBody TransferRequest request) {
        log.info("Transferring {} from wallet {} to {}",
                request.amount(), request.sourceWalletId(), request.targetWalletId());

        Wallet[] result = walletUseCase.transferBetweenWallets(
                request.sourceWalletId(),
                request.targetWalletId(),
                request.amount(),
                request.description()
        );

        return ResponseEntity.ok(ApiResponse.success("Transfer completed successfully", result));
    }

    @Operation(
            summary = "Get wallets by type",
            description = "Retrieves all wallets of a specific type for the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Wallets retrieved successfully",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Wallet>>> getWalletsByType(
            @Parameter(description = "Wallet type (e.g., CASH, BANK, CREDIT_CARD)", required = true, example = "CASH")
            @PathVariable WalletType type) {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        log.info("Fetching {} wallets for user: {}", type, userId);
        List<Wallet> wallets = walletUseCase.getWalletsByType(userId, type);
        return ResponseEntity.ok(ApiResponse.success("Wallets retrieved successfully", wallets));
    }

    @Operation(
            summary = "Set default wallet",
            description = "Sets or unsets a wallet as the default wallet for the user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Default status updated successfully",
                    content = @Content(schema = @Schema(implementation = Wallet.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<Wallet>> setDefaultWallet(
            @Parameter(description = "Wallet ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Set as default wallet", example = "true")
            @RequestParam(defaultValue = "true") boolean isDefault) {
        log.info("Setting wallet {} as default: {}", id, isDefault);
        Wallet wallet = walletUseCase.setDefaultWallet(id, isDefault);
        return ResponseEntity.ok(ApiResponse.success("Wallet default status updated successfully", wallet));
    }

    @Operation(
            summary = "Get total balance",
            description = "Calculates and returns the total balance across all wallets for the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Total balance calculated successfully",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))
            )
    })
    @GetMapping("/balance/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBalance() {
        // TODO: Get userId from authentication context
        Long userId = 1L;
        log.debug("Calculating total balance for user: {}", userId);
        BigDecimal totalBalance = walletUseCase.getTotalBalance(userId);
        return ResponseEntity.ok(ApiResponse.success("Total balance retrieved successfully", totalBalance));
    }
}