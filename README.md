개요
==============
기본적인 채팅 기능을 가지고 있는 채팅 애플리케이션  
많은 사람들이 사용한다는 가정하에 확장성을 고려해 만들었다

-------------

사용한 언어 & 기술
==============
* Java 17
* SpringBoot 3.1.1
* MySQL 8.1.0
* Apache Cassandra 4.1.3
* Redis 7.2.4
* Apache Kafka 3.6.1

--------------

기능
==============
* 회원 기능 : 회원 가입, 로그인, 비밀번호 변경 등 기본적인 회원과 관련된 기능
* 어드민 기능 : 가입한 회원 목록 보기, 강퇴 등의 어드민 기능
* 채널 기능 : 채널(채팅방)만들기, 채널 입장, 채널 퇴장
* 메시지 기능 : 메시지 전송, 메시지 수신

----------------

시스템 아키텍쳐
============
<img src="https://github.com/southbell2/chat-project/blob/master/images/chat-architecture.jpg" width="700" height="600">

1. Chat-App : 회원 기능, 채널 기능 등 채팅 메시지 송수신 이외의 기능들을 담당한다.
2. MySQL : 회원, 채팅방, 채팅방에 입장한 회원 데이터를 저장한다.
3. Redis : Refresh Token을 저장하는 용도
4. Cassandra : 채팅 메시지를 저장하는 용도
5. Message : 클라이언트와 웹소켓으로 연결되어 있어서 메시지를 송수신 한다.
6. Kafka : 이벤트로 발행된 메시지를 다루는 이벤트 스트리밍 플랫폼
7. Message Consumer : 카프카 컨슈머로 메시지를 카산드라에 저장하고 Redis에 발행한다.
8. Redis : Redis Pub/Sub 기능을 활용해 메시지를 송신한다.

------------

데이터베이스 스키마
=============
## MySQL
<img src="https://github.com/southbell2/chat-project/blob/master/images/chat-rdb.png" width="900" height="500">

```sql
CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname CHAR(10) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT NOW(),
  INDEX email_idx (email)
);

CREATE TABLE IF NOT EXISTS user_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role ENUM('ROLE_ADMIN', 'ROLE_USER'),
  INDEX user_role_idx (user_id),
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS channels (
  channel_id BIGINT PRIMARY KEY,
  created_at TIMESTAMP DEFAULT NOW(),
  master_id BIGINT NOT NULL,
  title VARCHAR(30) NOT NULL,
  total_count INT DEFAULT 0
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
```

## Cassandra

```sql
CREATE TABLE IF NOT EXISTS test.messages (
channel_id bigint,
bucket int,
message_id bigint,
nickname text,
content text,
created_at timestamp,
PRIMARY KEY ((channel_id, bucket), message_id)) WITH CLUSTERING ORDER BY (message_id DESC)
```

------------------------

고려사항 및 개선사항
=================

