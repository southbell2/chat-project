CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname CHAR(10) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role VARCHAR(50) CHECK (role IN ('ROLE_ADMIN', 'ROLE_USER')),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS channels (
  channel_id BIGINT PRIMARY KEY,
  created_at TIMESTAMP DEFAULT NOW(),
  master_id BIGINT NOT NULL,
  title VARCHAR(30) NOT NULL,
  total_count INT
);

CREATE TABLE IF NOT EXISTS entries (
  channel_id BIGINT,
  user_id BIGINT,
  joined_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (channel_id, user_id),
  INDEX user_id_idx (user_id),
  FOREIGN KEY (channel_id) REFERENCES channels(channel_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);