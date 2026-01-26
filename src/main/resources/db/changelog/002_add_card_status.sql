CREATE TYPE card_status AS ENUM (
    'ACTIVE',
    'BLOCKED',
    'EXPIRED'
);

ALTER TABLE cards ADD COLUMN status card_status NOT NULL DEFAULT 'ACTIVE';

UPDATE cards
SET status = 'EXPIRED'
WHERE expiration_date < CURRENT_DATE;
