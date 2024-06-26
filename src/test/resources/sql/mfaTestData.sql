DROP TABLE IF EXISTS mfa_token CASCADE;

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

INSERT INTO mfa_token (id, email, code, expiration_time, time_of_generation, time_of_delivery)
VALUES
    (2, 'user@example.com', '888888', '2024-07-03 11:58:00', '2024-07-03 11:55:00', '2024-07-03 11:56:00'),
    (3, 'user1@example.com', '654321', '2021-07-01 11:59:00', '2024-07-01 11:58:00', '2024-07-01 11:59:05'),
    (4, 'user2@example.com', '968574','2099-12-12 11:59:00', '2024-07-01 11:58:00', '2024-07-01 11:59:05');