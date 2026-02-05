DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS transaction_histories;
DROP TABLE IF EXISTS transactions;

CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       name VARCHAR(50) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction_histories (
                                       transaction_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       sender_user_id BIGINT NOT NULL,
                                       receiver_user_id BIGINT,
                                       amount BIGINT,
                                       type VARCHAR(50) NOT NULL
);

CREATE TABLE transactions (
                              transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              sender_id BIGINT NOT NULL,
                              account_number VARCHAR(50) NOT NULL,
                              amount BIGINT,
                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                              transacted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT unique_pending_transaction UNIQUE (sender_id, status)
)