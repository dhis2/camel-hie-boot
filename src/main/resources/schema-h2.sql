CREATE TABLE IF NOT EXISTS MESSAGE_STORE (
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    key_            VARCHAR NOT NULL,
    headers         JSON NOT NULL,
    body            VARCHAR,
    context         VARCHAR,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);