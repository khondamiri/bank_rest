package org.example.bank_rest.dto.card;

import lombok.Builder;
import lombok.Data;
import org.example.bank_rest.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CardResponse {
    private Long id;
    private String cardLast4;
    private String cardholderName;
    private LocalDate expirationDate;
    private BigDecimal balance;
    private CardStatus status;
    private Long userId;
}