-- Table for storing user information
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Table for storing contacts
CREATE TABLE contacts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

-- Table for storing email addresses
CREATE TABLE emails (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    contact_id BIGINT NOT NULL REFERENCES contacts (id) ON DELETE CASCADE
);

-- Table for storing phone numbers
CREATE TABLE phone_numbers (
    id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    contact_id BIGINT NOT NULL REFERENCES contacts (id) ON DELETE CASCADE
);
