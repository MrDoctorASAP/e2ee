
CREATE TYPE CHAT_KIND AS ENUM ('personal', 'secure', 'group');
CREATE TYPE MESSAGE_KIND AS ENUM('plain', 'secure');

CREATE TABLE user_details (
  id BIGSERIAL PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  info VARCHAR(255)
);

CREATE TABLE user_credentials (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES user_details(id),
  username VARCHAR(255) UNIQUE NOT NULL,
  password TEXT NOT NULL
);

CREATE TABLE chat(
  id BIGSERIAL PRIMARY KEY,
  kind CHAT_KIND NOT NULL,
  creation_date BIGINT NOT NULL
);

CREATE TABLE personal_chat(
  id BIGSERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL REFERENCES chat(id)
);

CREATE TABLE group_chat(
  id BIGSERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL REFERENCES chat(id),
  owner_id BIGINT NOT NULL REFERENCES user_details(id),
  group_name VARCHAR(255) NOT NULL
);

CREATE TABLE secure_chat(
  id BIGSERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL REFERENCES chat(id),
  secure_id VARCHAR(36) NOT NULL UNIQUE
);

CREATE TABLE chat_member(
  id BIGSERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL REFERENCES chat(id),
  member_id BIGINT NOT NULL REFERENCES user_details(id)
);

CREATE TABLE message(
  id BIGSERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL REFERENCES chat(id),
  sender_id BIGINT NOT NULL REFERENCES user_details(id),
  kind MESSAGE_KIND NOT NULL,
  data TEXT
);

CREATE TABLE message_event(
  id BIGSERIAL PRIMARY KEY,
  message_id BIGINT NOT NULL REFERENCES message(id),
  recipient_id BIGINT NOT NULL REFERENCES user_details(id)
);

CREATE TABLE secure_chat_invite(
  id BIGSERIAL PRIMARY KEY,
  secure_id TEXT NOT NULL REFERENCES secure_chat(secure_id),
  sender_id BIGINT NOT NULL REFERENCES user_details(id),
  recipient_id BIGINT NOT NULL REFERENCES user_details(id),
  public_key TEXT NOT NULL
);

CREATE TABLE secure_chat_accept(
  id BIGSERIAL PRIMARY KEY,
  secure_id TEXT NOT NULL REFERENCES secure_chat(secure_id),
  public_key TEXT NOT NULL
);

CREATE UNIQUE INDEX username_index ON user_details (username);
CREATE UNIQUE INDEX secure_id_index ON secure_chat (secure_id);
CREATE INDEX invite_secure_id_index ON secure_chat_invite (secure_id);
CREATE INDEX accept_secure_id_index ON secure_chat_accept (secure_id);
