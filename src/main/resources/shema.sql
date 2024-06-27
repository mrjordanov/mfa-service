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