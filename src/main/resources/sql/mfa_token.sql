CREATE TABLE mfa_token (
   id BIGSERIAL PRIMARY KEY,
   email VARCHAR(255) NOT NULL,
   code VARCHAR(255) NOT NULL,
   expiration_time TIMESTAMP without time zone  NOT NULL,
   time_of_generation TIMESTAMP without time zone  NOT NULL,
   time_of_delivery TIMESTAMP without time zone,

   CONSTRAINT unique_email_code UNIQUE (email, code),
   CONSTRAINT unique_code_expiration UNIQUE (code, expiration_time)
);

INSERT INTO mfa_token (email, code, expiration_time, time_of_generation, time_of_delivery)
VALUES
    ('user1@example.com', '123456', '2024-06-30 12:00:00', '2024-06-30 11:45:00', '2024-06-30 11:55:00'),
    ('user2@example.com', '654321', '2024-07-01 09:00:00', '2024-07-01 08:45:00', NULL),
    ('user3@example.com', '987654', '2024-07-02 15:30:00', '2024-07-02 15:15:00', NULL);