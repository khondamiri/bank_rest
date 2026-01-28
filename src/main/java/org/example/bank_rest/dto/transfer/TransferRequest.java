package org.example.bank_rest.dto.transfer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotNull(message = "Карта отправителя")
    private Long fromCardId;

    @NotNull(message = "Карта для пополнения")
    private Long toCardId;

    @NotNull(message = "Сумма не может быть нулевой")
    @Positive(message = "Сумма не может быть негативной")
    private BigDecimal amount;
}