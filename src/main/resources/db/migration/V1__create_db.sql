CREATE TABLE IF NOT EXISTS link (
	short_url varchar(8) PRIMARY KEY,
	original_url varchar(2048) NOT NULL,
	created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	expired_at timestamp NOT NULL,
	visit_count bigint NOT NULL DEFAULT 0
);

-- test test