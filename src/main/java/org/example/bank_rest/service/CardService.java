package org.example.bank_rest.service;

import lombok.RequiredArgsConstructor;
import org.example.bank_rest.dto.card.CardResponse;
import org.example.bank_rest.dto.card.CreateCardRequest;
import org.example.bank_rest.entity.Card;
import org.example.bank_rest.entity.CardStatus;
import org.example.bank_rest.entity.User;
import org.example.bank_rest.exception.AccessDeniedException;
import org.example.bank_rest.exception.InactiveCardStatusException;
import org.example.bank_rest.exception.ResourceNotFoundException;
import org.example.bank_rest.repository.CardRepository;
import org.example.bank_rest.repository.UserRepository;
import org.example.bank_rest.util.CryptoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CryptoUtil cryptoUtil;

    @Transactional
    public CardResponse createCard(CreateCardRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        String encryptedNumber = cryptoUtil.encrypt(request.getCardNumber());
        String last4 = request.getCardNumber().substring(12);

        if (cardRepository.findByCardNumberEnc(encryptedNumber).isPresent()) {
            throw new IllegalArgumentException("Card with this number already exists");
        }

        Card card = Card.builder()
                .user(user)
                .cardNumberEnc(encryptedNumber)
                .cardLast4(last4)
                .cardholderName(request.getCardholderName())
                .expirationDate(request.getExpirationDate())
                .balance(BigDecimal.ZERO)
                .status(CardStatus.ACTIVE)
                .createdAd(LocalDateTime.now())
                .build();

        return mapToDto(cardRepository.save(card));
    }

    public Page<CardResponse> getCards(Long userId, String username, Pageable pageable) {
        if (userId != null) {
            return cardRepository.findAllByUserId(userId, pageable).map(this::mapToDto);
        } else if (username != null) {
            return cardRepository.findAllByUserUsername(username, pageable).map(this::mapToDto);
        } else {
            return cardRepository.findAll(pageable).map(this::mapToDto);
        }
    }

    @Transactional
    public void updateCardStatus(Long cardId, CardStatus newStatus, String currentUsername, boolean isAdmin) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!isAdmin) {
            if (!card.getUser().getUsername().equals(currentUsername)) {
                throw new AccessDeniedException("You do not own this card");
            }
            if (newStatus != CardStatus.BLOCKED) {
                throw new AccessDeniedException("Users are only allowed to block cards");
            }
        }
        card.setStatus(newStatus);
        cardRepository.save(card);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new ResourceNotFoundException("Card not found with ID: " + cardId);
        }
        cardRepository.deleteById(cardId);
    }

    @Transactional
    public CardResponse fundCard(Long cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + cardId));

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InactiveCardStatusException("Cannot fund a card that is not active. Current status: " + card.getStatus());
        }

        BigDecimal newBalance = card.getBalance().add(amount);
        card.setBalance(newBalance);

        Card savedCard = cardRepository.save(card);

        return mapToDto(savedCard);
    }

    private CardResponse mapToDto(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardLast4("**** **** **** " + card.getCardLast4())
                .cardholderName(card.getCardholderName())
                .expirationDate(card.getExpirationDate())
                .balance(card.getBalance())
                .status(card.getStatus())
                .userId(card.getUser().getId())
                .build();
    }
}