package org.example.bank_rest.dto.card;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCardRequest {
    @NotNull(message = "ID пользователя")
    private Long userId;

    @NotBlank(message = "Номер карты")
    @Pattern(regexp = "\\d{16}", message = "Должно быть 16 цифр")
    private String cardNumber;

    @NotBlank(message = "Имя владельца карты")
    private String cardholderName;

    @NotNull(message = "Срок действия")
    @Future(message = "Срок действия должен быть в будущем")
    private LocalDate expirationDate;
}