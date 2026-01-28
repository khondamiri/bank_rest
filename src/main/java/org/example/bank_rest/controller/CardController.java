package org.example.bank_rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bank_rest.dto.card.CardResponse;
import org.example.bank_rest.dto.card.CreateCardRequest;
import org.example.bank_rest.entity.CardStatus;
import org.example.bank_rest.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Карты", description = "Операции связанные с картами")
public class CardController {

    private final CardService cardService;

    // ADMIN ENDPOINTS

    @Operation(summary = "Админ: создать карту")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<CardResponse> createCard(@RequestBody @Valid CreateCardRequest request) {
        return ResponseEntity.ok(cardService.createCard(request));
    }

    @Operation(summary = "Админ: все карты, можно фильтровать по user id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(cardService.getCards(userId, null, pageable));
    }

    @Operation(summary = "Админ: изменить статус карты (Block|Activate|Expire)")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<Void> updateStatusAdmin(
            @PathVariable Long id,
            @RequestParam CardStatus status
    ) {
        cardService.updateCardStatus(id, status, null, true);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Админ: Удалить карту")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    // USER ENDPOINTS

    @Operation(summary = "Пользователь: показать мои карты")
    @GetMapping("/my")
    public ResponseEntity<Page<CardResponse>> getMyCards(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(cardService.getCards(null, authentication.getName(), pageable));
    }

    @Operation(summary = "Пользователь: блокировать мою карту")
    @PostMapping("/my/{id}/block")
    public ResponseEntity<Void> blockMyCard(
            @PathVariable Long id,
            Authentication authentication
    ) {
        cardService.updateCardStatus(id, CardStatus.BLOCKED, authentication.getName(), false);
        return ResponseEntity.ok().build();
    }
}