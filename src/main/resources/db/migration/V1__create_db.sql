CREATE TABLE IF NOT EXISTS link (
	id BIGSERIAL PRIMARY KEY,
	short_url varchar(8) NOT NULL UNIQUE,
	original_url varchar(2048) NOT NULL,
	created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	expired_at timestamp NOT NULL,
	visit_count bigint NOT NULL DEFAULT 0
);