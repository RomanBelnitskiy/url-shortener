UPDATE link
SET user_id = (SELECT MIN(u.id) FROM users AS u);