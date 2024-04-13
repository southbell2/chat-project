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

시스템 아키텍쳐
============
<img src="https://github.com/southbell2/chat-project/blob/master/images/chat-architecture.jpg" width="700" height="600">

1. Chat-App : 회원 기능, 채팅방 기능 등 채팅 메시지 송수신 기능 이외의 기능들을 담당한다.
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
