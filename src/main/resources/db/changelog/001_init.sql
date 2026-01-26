CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    card_number_enc VARCHAR(512) NOT NULL,
    card_last4 VARCHAR(4) NOT NULL,
    cardholder_name VARCHAR(100) NOT NULL,
    expiration_date DATE NOT NULL,
    balance NUMERIC(15,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_cards_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_cards_user_id ON cards(user_id);

CREATE TABLE transfers (
    id BIGSERIAL PRIMARY KEY,
    from_card_id BIGINT NOT NULL,
    to_card_id BIGINT NOT NULL,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT fk_transfer_from FOREIGN KEY (from_card_id) REFERENCES cards(id),
    CONSTRAINT fk_transfer_to FOREIGN KEY (to_card_id) REFERENCES cards(id)
);

CREATE INDEX idx_transfers_from_card ON transfers(from_card_id);
CREATE INDEX idx_transfers_to_card ON transfers(to_card_id);