package org.example.bank_rest;

import org.example.bank_rest.dto.transfer.TransferRequest;
import org.example.bank_rest.entity.Card;
import org.example.bank_rest.entity.CardStatus;
import org.example.bank_rest.entity.Transfer;
import org.example.bank_rest.entity.User;
import org.example.bank_rest.exception.AccessDeniedException;
import org.example.bank_rest.repository.CardRepository;
import org.example.bank_rest.repository.TransferRepository;
import org.example.bank_rest.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    private User testUser;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        fromCard = Card.builder()
                .id(101L)
                .user(testUser)
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("100.00"))
                .build();

        toCard = Card.builder()
                .id(102L)
                .user(testUser)
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("50.00"))
                .build();
    }

    @Test
    void performTransfer_whenSuccessful_shouldUpdateBalancesAndLogTransfer() {
        // ARRANGE
        TransferRequest request = new TransferRequest();
        request.setFromCardId(fromCard.getId());
        request.setToCardId(toCard.getId());
        request.setAmount(new BigDecimal("25.00"));

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        // ACT
        transferService.performTransfer(request, testUser.getUsername());

        // ASSERT
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transferRepository, times(1)).save(any(Transfer.class));

        assertEquals(new BigDecimal("75.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("75.00"), toCard.getBalance());
    }

    @Test
    void performTransfer_whenInsufficientFunds_shouldThrowIllegalStateException() {
        // ARRANGE
        TransferRequest request = new TransferRequest();
        request.setFromCardId(fromCard.getId());
        request.setToCardId(toCard.getId());
        request.setAmount(new BigDecimal("200.00"));

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        // ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> {
            transferService.performTransfer(request, testUser.getUsername());
        });

        verify(cardRepository, never()).save(any(Card.class));
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void performTransfer_whenUserDoesNotOwnSourceCard_shouldThrowAccessDeniedException() {
        // ARRANGE
        User anotherUser = User.builder().id(2L).username("another_user").build();
        fromCard.setUser(anotherUser);

        TransferRequest request = new TransferRequest();
        request.setFromCardId(fromCard.getId());
        request.setToCardId(toCard.getId());
        request.setAmount(new BigDecimal("10.00"));

        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));

        // ACT & ASSERT
        assertThrows(AccessDeniedException.class, () -> {
            transferService.performTransfer(request, testUser.getUsername());
        });

        verify(cardRepository, never()).save(any(Card.class));
    }
}