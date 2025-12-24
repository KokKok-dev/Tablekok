# 🍽️ TableKok : MSA 기반 음식점 통합 예약 플랫폼

<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/925e2a68-f38e-45da-b033-8aa31a764219" />


> **"고객과 점주 모두를 위한 올인원 예약 솔루션"** > 일반 예약부터 인기 맛집 실시간 대기열(Queue), 현장 웨이팅까지 한 번에 관리하는 MSA 기반 서비스입니다.

<br>

## 📖 프로젝트 소개
**TableKok**은 음식점 예약, 웨이팅, 리뷰 관리를 하나의 플랫폼에서 제공하여 파편화된 예약 경험을 통합합니다.
단순한 예약 기능을 넘어, **대규모 트래픽이 발생하는 인기 맛집의 대기열 처리**와 **실시간 현장 웨이팅** 문제를 기술적으로 해결하는 데 초점을 맞추었습니다.

### 🧑‍💻 팀원 구성

| 이름 | 역할 | 담당 도메인 및 기여 | GitHub |
|:---:|:---:|:---|:---:|
| **태성원** | 팀장 | 🔐 Auth, 🚪 Gateway(Auth), 🏗 Infra | [Link](https://github.com/trevivom76) |
| **이가현** | 부팀장 | 🏪 Store, ⏳ Waiting | [Link](https://github.com/gashine20) |
| **송준일** | 팀원 | 🔍 Search, ⭐ Review, 🚪 Gateway(Routing) | [Link](https://github.com/thdwnsdlf61) |
| **황교석** | 팀원 | 📅 Reservation(Normal/Queue) | [Link](https://github.com/gyoseok17) |


### 📅 프로젝트 기간
* 2025.11.24 ~ 2025.12.26(5주)


### 🌟 핵심 목표
* **사용자 경험 통합:** 예약, 웨이팅, 리뷰를 하나의 앱에서 처리
* **대용량 트래픽 처리:** 인기 맛집 예약 시 발생하는 동시성 이슈 및 서버 부하 해결
* **실시간성 보장:** SSE와 Kafka를 활용한 실시간 알림 및 데이터 동기화
* **검색 성능 최적화:** Elasticsearch + Nori 분석기를 이용한 빠르고 정확한 한글 검색

<br>

## 🧬 인프라 설계도

### [인프라 아키텍처]
<img width="1801" height="1131" alt="image" src="https://github.com/user-attachments/assets/b0e66817-fdc2-4d1b-ad6c-169a24ee4d5c" />

### [CI/CD]
<img width="11007" height="1172" alt="image" src="https://github.com/user-attachments/assets/c2e1e2b9-03a9-4336-ac1b-629e72201708" />

### [시스템 아키텍처]
<img width="2148" height="1481" alt="image" src="https://github.com/user-attachments/assets/a1748c4b-1f0e-40f9-baa7-921beb485790" />

### [ERD]
<img width="1791" height="1186" alt="image" src="https://github.com/user-attachments/assets/d1676138-aa69-4004-858b-13975852b2a3" />

<br>

## 🛠 기술 스택 (Tech Stack)
![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.8-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-ORM-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-MSA-6DB33F?style=flat-square&logo=spring&logoColor=white)

![Kafka](https://img.shields.io/badge/Apache_Kafka-Event_Driven-231F20?style=flat-square&logo=apachekafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Caching_&_Queue-DC382D?style=flat-square&logo=redis&logoColor=white)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-Search_Engine-005571?style=flat-square&logo=elasticsearch&logoColor=white)
![SSE](https://img.shields.io/badge/SSE-Realtime-000000?style=flat-square&logo=html5&logoColor=white)

![AWS EC2](https://img.shields.io/badge/Amazon_EC2-Compute-FF9900?style=flat-square&logo=amazon-aws&logoColor=white)
![AWS RDS](https://img.shields.io/badge/Amazon_RDS-Database-527FFF?style=flat-square&logo=amazon-rds&logoColor=white)
![AWS ECS](https://img.shields.io/badge/Amazon_ECS-Container-FF9900?style=flat-square&logo=amazon-aws&logoColor=white)
![AWS ECR](https://img.shields.io/badge/Amazon_ECR-Registry-FF9900?style=flat-square&logo=amazon-aws&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI%2FCD-2088FF?style=flat-square&logo=github-actions&logoColor=white)

![JMeter](https://img.shields.io/badge/Apache_JMeter-Performance_Test-D22128?style=flat-square&logo=apachejmeter&logoColor=white)
![JitPack](https://img.shields.io/badge/JitPack-Library_Dist-81C784?style=flat-square&logo=jitpack&logoColor=white)

![Slack](https://img.shields.io/badge/Slack-Communication-4A154B?style=flat-square&logo=slack&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-Communication-5865F2?style=flat-square&logo=discord&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-Team_Space-000000?style=flat-square&logo=notion&logoColor=white)

| 구분 | 기술 (Technology) |
|:---:|:---|
| **Language & Framework** | Java 17, Spring Boot 3.2, Spring Cloud (Gateway, Eureka, OpenFeign) |
| **Database** | PostgreSQL, Redis (Caching, Pub/Sub, ZSet), Elasticsearch |
| **Message Queue** | Apache Kafka (Event-Driven Architecture) |
| **DevOps & Infra** | Docker, Zipkin (Distributed Tracing), GitHub Actions (CI/CD) |
| **Tools** | QueryDSL, SSE (Server-Sent Events), JWT |

<br>

## ✨ 주요 기능 (Key Features)

### 1️⃣ 인증 및 회원 관리 (Auth Service)
* **통합 회원가입:** 사업자 번호 유무에 따른 Role 자동 부여 (CUSTOMER / OWNER)
* **보안:** JWT Access/Refresh Token 기반 인증 및 Redis Blacklist를 통한 로그아웃 처리
* **Gateway:** `GlobalFilter`를 통한 JWT 파싱 및 `X-User-Id` 헤더 주입

### 2️⃣ 음식점 관리 및 검색 (Store & Search Service)
* **CQRS 패턴 적용:** Command(PostgreSQL)와 Query(Elasticsearch) 책임 분리
* **데이터 동기화:** Kafka를 통해 Store 변경 사항을 Search 서비스로 실시간 전파 (Eventual Consistency)
* **고성능 검색:** Elasticsearch Nori 분석기 활용 및 `search_after` 기반 Cursor 페이징 구현

### 3️⃣ 예약 및 웨이팅 시스템 (Reservation & Waiting Service)
* **일반 예약:** 트랜잭션 보장을 통한 중복 예약 방지 및 정책 검증
* **인기 맛집 예약 (대기열):** Redis `ZSet`을 활용한 대기열 시스템 (순번 보장, 진입 제어)
* **현장 웨이팅:** 실시간 줄서기 및 예상 대기시간 계산, SSE를 통한 입장 알림

### 4️⃣ 리뷰 및 커뮤니티 (Review Service)
* **신뢰성 확보:** 방문 완료(DONE) 된 예약에 한해서만 리뷰 작성 가능
* **통계 최적화:** Kafka를 활용해 리뷰 작성 시 평균 평점/리뷰 수를 비동기로 집계하여 Store 서비스에 반영

<br>

## 🗣️ 기술적 의사결정 (Technical Decisions)

<details>
<summary><strong>1. Monorepo vs Polyrepo: 모노레포 선택</strong></summary>

* **배경:** 4인의 소규모 팀으로 빠른 개발과 협업 효율성이 필요함.
* **선택:** **Monorepo**
* **이유:** 공통 설정(Git Convention, Build Logic) 유지 비용 감소 및 팀원 간 전체 아키텍처 이해도 증진을 위함.
</details>

<details>
<summary><strong>2. CQRS 패턴과 Kafka 도입</strong></summary>

* **문제:** 음식점 도메인의 복잡한 조인 연산과 조회 트래픽(Read)이 쓰기(Write) 트래픽보다 압도적으로 높음(8:2 비율).
* **해결:**
    * **CQRS:** Store Service(Write/RDBMS)와 Search Service(Read/Elasticsearch) 분리.
    * **Kafka:** 서비스 간 느슨한 결합을 유지하며 데이터 최종 일관성 보장. 장애 발생 시 메시지 유실 방지.
</details>

<details>
<summary><strong>3. Redis ZSet을 활용한 대기열 관리</strong></summary>

* **문제:** RDBMS `COUNT(*)`로 대기 순번을 계산할 경우 대량 트래픽 시 DB 부하 심각.
* **해결:** **Redis Sorted Set (ZSet)** 도입.
* **성과:** 대기 순번 확인 시간 복잡도를 $O(\log N)$으로 단축하여 실시간 응답 속도 개선.
</details>

<br>

## 🏀 트러블슈팅 (Troubleshooting) & 성능 개선
<details>
<summary><strong>1. 인기 맛집 예약 트래픽 제어 (Distributed Queue)</strong></summary>
  
* **상황:** 인기 매장 예약 오픈 시 트래픽 폭주로 인한 Connection Refused 및 응답 지연 발생 (평균 446ms, 오류율 0.91%).
* **해결:** Redis `ZSet` 기반의 대기열 시스템 도입.
* **진입 제어:** 스케줄러가 'Score=예약제한시간'을 기준으로 N명씩만 예약 가능 상태로 전환.
* **최적화:** 대기열 진입 시에만 순위를 계산하고, 이후 알림은 '입장 인원 수'만 전달하여 클라이언트가 순위를 계산하도록 하여 서버 연산 최소화.
* **결과:** 안정적인 트래픽 처리 및 서버 리소스 효율화 달성.
</details>

<details>
<summary><strong>2. 웨이팅 번호 발급 동시성 이슈 해결</strong></summary>
* **문제:** JMeter 테스트 시 동시 요청에 대해 중복된 웨이팅 번호가 발급되는 현상 발생.
* **시도 1 (DB Lock):** `PESSIMISTIC_WRITE` 락 적용 → 정합성은 해결되었으나 대기 시간 누적으로 성능 저하 (72ms).
* **시도 2 (Redis Atomic):** **Redis `INCR` 명령어 사용**.
* **결과:** 락 없이 원자성(Atomicity)을 보장하며 처리 속도 **72ms → 0.002ms**로 획기적 개선.
</details>

<details>
<summary><strong>3. 리뷰 조회 성능 최적화 (Index & Cursor Paging)</strong></summary>
* **문제:** 100만 건 데이터 조회 시 Full Table Scan 발생 (61.96ms), Deep Pagination 시 성능 저하.
* **해결:**
    1.  복합 인덱스 적용 (`idx_store_created_at`).
    2.  `Offset` 방식 대신 `Cursor` 기반 페이징(No-Offset) 도입.
* **결과:** 쿼리 실행 시간 **32.45ms → 0.025ms (약 1,298배 개선)** 및 일정한 조회 속도 $O(1)$ 보장.
</details>

<br>

## 서비스별 포트번호
| 서비스명 | 포트번호 |
|:---:|:---:|
| **Eureka Server** | 19090 |
| **API Gateway** | 19091 |
| **User-service** | 8081 |
| **Store-service** | 8082 |
| **Search-service** | 8083 |
| **Reservation-service** | 8084 |
| **Hot-Reservation-service** | 8085 |
| **Waiting-service** | 8086 |
| **Review-service** | 8087 |

## 💾 설치 및 실행 방법 (Getting Started)

1. **Repository Clone**
   ```bash
   git clone [https://github.com/username/TableKok.git](https://github.com/username/TableKok.git)
