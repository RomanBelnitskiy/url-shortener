CREATE TABLE IF NOT EXISTS url (
	id BIGSERIAL PRIMARY KEY,
	short_url varchar NOT NULL UNIQUE,
	original_url varchar NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	expired_at timestamp NOT NULL,
	visit_count bigint DEFAULT 0
);