package org.example.bank_rest.service;

import lombok.RequiredArgsConstructor;
import org.example.bank_rest.dto.transfer.TransferRequest;
import org.example.bank_rest.entity.Card;
import org.example.bank_rest.entity.CardStatus;
import org.example.bank_rest.entity.Transfer;
import org.example.bank_rest.exception.AccessDeniedException;
import org.example.bank_rest.exception.InactiveCardStatusException;
import org.example.bank_rest.exception.ResourceNotFoundException;
import org.example.bank_rest.repository.CardRepository;
import org.example.bank_rest.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;

    @Transactional
    public void performTransfer(TransferRequest request, String currentUsername) {
        if (request.getFromCardId().equals(request.getToCardId())) {
            throw new IllegalArgumentException("Source and destination cards cannot be the same.");
        }

        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Source card not found"));
        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination card not found"));

        if (!fromCard.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You do not have permission to transfer from this card.");
        }

        if (!toCard.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You can only transfer to your own cards.");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new InactiveCardStatusException("Both cards must be active to perform a transfer.");
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds on the source card.");
        }

        BigDecimal fromNewBalance = fromCard.getBalance().subtract(request.getAmount());
        BigDecimal toNewBalance = toCard.getBalance().add(request.getAmount());

        fromCard.setBalance(fromNewBalance);
        toCard.setBalance(toNewBalance);

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        Transfer transferLog = Transfer.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(request.getAmount())
                .build();
        transferRepository.save(transferLog);
    }
}