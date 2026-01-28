package org.example.bank_rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank
    @Size(min = 3, max = 15, message = "Имя пользователя должно быть между 3 и 15 знаками")
    private String username;

    @NotBlank
    @Size(min = 6, message = "Пароль должно содержать минимум 6 знакам")
    private String password;
}
