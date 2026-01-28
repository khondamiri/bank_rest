package org.example.bank_rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InactiveCardStatusException extends RuntimeException {
    public InactiveCardStatusException(String message) {
        super(message);
    }
}
