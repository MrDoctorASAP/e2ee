CREATE TABLE IF NOT EXISTS user_details (
  "id" BIGSERIAL PRIMARY KEY,
  "username" VARCHAR(256) UNIQUE NOT NULL,
  "password" VARCHAR(256) NOT NULL
);

CREATE UNIQUE INDEX username_index ON user_details(username);

CREATE USER auth_service WITH PASSWORD 'WfukjUWlNG62s8QCuCnpbop3Hj8gbrzL';
GRANT ALL PRIVILEGES ON user_details TO auth_service;
GRANT ALL PRIVILEGES ON user_details_id_seq TO auth_service;

curl -H "Content-Type: application/json" --request POST --data '{"username":"admin","password":"admin"}' http://localhost:8080/api/v1/auth/register
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3ODk1NjY1MSwiZXhwIjoxNjc4OTkyNjUxfQ.HIiZ3X8UVZ2nYTR6Jkyids7TMro94f1LfojtRkZ1Z-k

curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3ODk1NjY1MSwiZXhwIjoxNjc4OTkyNjUxfQ.HIiZ3X8UVZ2nYTR6Jkyids7TMro94f1LfojtRkZ1Z-k"
--request POST http://localhost:8080/api/v1/auth/extend