package org.example.bank_rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bank_rest.dto.transfer.TransferRequest;
import org.example.bank_rest.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfers")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Переводы", description = "Операции для перевода между картами")
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "Пользователь: перевод между картами")
    @PostMapping
    public ResponseEntity<Void> transferFunds(
            @RequestBody @Valid TransferRequest request,
            Authentication authentication
    ) {
        transferService.performTransfer(request, authentication.getName());
        return ResponseEntity.ok().build();
    }
}