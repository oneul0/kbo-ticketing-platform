# 실제 Redis 대기열 동시성 검증 — 2026-10-04

개인 저장소 `https://github.com/oneul0/kbo-ticketing-platform.git`의 `dev` 최신 checkout
`c8b04ddbebd0c5afd72807cb9d0411f65b475ee2`를 기준으로 수행했다.
작업 브랜치는 `test/redis-queue-concurrency`이다. 별도 clone에서 작업했으며 기존 checkout과 조직 저장소를 수정하지 않았다.
초기 검증은 로컬에서 수행했다. 이후 사용자 승인에 따라 개인 저장소의 별도 브랜치와 검토용 초안 PR로 제출한다.
조직 저장소를 PR 대상으로 사용하지 않으며 merge·deploy는 수행하지 않는다.

저장소에 `AGENTS.md`, `.agents/skills`, 상세 실행 문서가 없었고 기존 README는 제목뿐이었다.
로컬 `.agents/skills` 카탈로그에도 이 작업에 해당하는 스킬은 없었다.
Java/Gradle 요구 사항, Redis 설정, 대기열 서비스·helper·AOP 및 기존 테스트를 직접 확인했다.

## 확인한 결함과 수정

기존 `QueueServiceConcurrencyTest`는 Redis helper, 순번 조회와 응답 매퍼를 mock으로 대체했다.
중복 거절도 mock 내부의 별도 Set 로직이 만들었으므로 실제 ZSet 등록의 정확성을 증명하지 못했다.

실제 helper는 `INCR` 뒤 `ZADD`를 별도로 수행했다. 이미 등록된 사용자도 성공으로 반환되며 기존 점수가 덮어써졌다.
서비스 락 없이 동시 호출하면 번호 발급 순서와 ZADD 완료 순서가 달라져 최종 점수가 달라졌다.
또한 `DistributedLockAop`는 업무 `GlobalException`까지 서버 오류로 변환했다.

수정 파일:

- `queue-service/src/main/java/com/boeingmerryho/business/queueservice/infrastructure/QueueRedisHelperImpl.java`: Lua로 중복 확인, 번호 발급, ZSet 등록, 최초 TTL 설정을 한 번에 처리한다. 중복은 `USER_ALREADY_IN_QUEUE`로 거절하며 점수·번호 카운터·TTL을 변경하지 않는다. 기존 RedisTemplate 직렬화를 유지한다.
- `queue-service/src/main/java/com/boeingmerryho/business/queueservice/config/aop/DistributedLockAop.java`: 업무 오류 코드를 보존한다. 따라서 중복 거절과 가게 비활성 오류가 호출자에게 그대로 전달된다. 기존 락 획득 실패의 업무 오류 코드 역시 보존된다.
- `queue-service/src/test/java/com/boeingmerryho/business/queueservice/application/QueueServiceConcurrencyTest.java`: 실제 Redis, 운영용 Redisson 설정·직렬화, 실제 helper·service·mapper·분산락 AOP를 사용한다. 가입 경로에서 사용하지 않는 persistence collaborator만 mock이다. 트랜잭션 AOP에는 테스트 전용 H2 transaction manager를 사용한다.
- `README.md`, 본 문서, `docs/verification/redis-queue-2026-10-04/`: 실행 안내와 개인 식별 메타데이터를 제거한 실행 결과, 집계 스크립트.

업무 규칙의 근거는 기존 테스트의 “단일 사용자 요청은 한 번만 처리” 설명과 이미 정의된 `Q-011 / USER_ALREADY_IN_QUEUE`이다.
현재 대기열에 남아 있는 사용자의 중복 요청을 거절하도록 수정했다.
삭제 후 재등록은 기존처럼 새 번호를 부여한다. 이 동작을 검증했으나, 취소·호출 완료 후 재등록 제한은 문서화된 정책이 없어 별도 판단이 필요하다.
이번 변경에서 금지 정책을 새로 도입하지 않았다.

응답 DTO의 `sequence` 필드는 기존 서비스에서 **현재 1부터 시작하는 rank**를 담는다.
예를 들어 1번 사용자가 삭제된 뒤 재등록하면 저장 sequence는 3, 응답 rank는 2다. 필드명을 변경하지 않았다.

## 환경과 격리 조건

- macOS 26.6.2 (25G83), arm64, Temurin Java 17.0.17, Gradle wrapper 8.13.
- Docker Desktop 4.71.0 / Engine 29.4.1, Linux arm64; Docker 보고값 CPU 8개, 메모리 8,321,994,752 bytes.
- Redis `6.2.17-alpine`, 이미지 digest `redis@sha256:148bb5411c184abd288d9aaed139c98123eeb8824c5d3fce03cf721db58066d8`.
- 실제 resolve된 Testcontainers 1.20.6, Redisson 3.45.1, Spring Boot 의존성 관리 3.4.4.
- 테스트 클래스당 새 Redis 컨테이너 1개, 무작위 호스트 포트, 재사용 없음, 기존 볼륨 없음, RDB/AOF 비활성화, 테스트 비밀번호만 사용.
- `@SpringJUnitConfig`로 필요한 bean만 구성한다. `application.yml`, config server, 운영 Redis/MySQL, Eureka를 로드하지 않는다.
- 매 테스트 전 해당 컨테이너의 DB만 초기화하고 현재 날짜에 맞는 실제 ticket/availability 키를 넣는다. 외부 Redis 주소를 받는 옵션이 없다.
- 10개 worker를 ready/start latch로 동시에 출발시킨다. Future 결과를 모두 수집하여 성공·명시적 중복 거절·예상 외 실패를 별도로 센다. 시간 제한과 executor 정리를 강제한다.
- 각 동시성 시나리오를 20회 반복한다. 테스트 메서드는 순차 실행된다. 서비스 경로는 기존 store 단위 분산락으로 직렬화된다. helper 직접 호출은 해당 락 없이 실행한다.

처음에는 Docker가 꺼져 있어 시작했다. 이후 기본 Docker API로는 Testcontainers 초기화가 실패했다.
테스트 JVM에 `-Dapi.version=1.44`를 적용하여 해결했다. Docker 설정이나 보안 파라미터는 변경하지 않았다.

## 결과

표의 수치는 **10개 요청으로 구성된 한 배치** 기준이다. 성공은 정상 응답, 거절은 `USER_ALREADY_IN_QUEUE`, 실패는 그 외 예외다.
“실패 0”은 업무 규칙이 올바르다는 뜻이 아니다. 수정 전 중복 배치는 잘못된 성공 10건을 반환해서 회귀 검증에 실패했다.

| 시나리오 | 수정 전 성공/거절/실패 | 수정 후 성공/거절/실패 | 수정 전 저장 인원/카운터 | 수정 후 저장 인원/카운터 |
|---|---:|---:|---:|---:|
| 서로 다른 10명, 실제 서비스·락 | 10 / 0 / 0 | 10 / 0 / 0 | 10 / 10 | 10 / 10 |
| 같은 사용자 10회, 실제 서비스·락 | 10 / 0 / 0 | 1 / 9 / 0 | 1 / 10 | 1 / 1 |
| 같은 사용자 10회, helper 직접 호출 | 10 / 0 / 0 | 1 / 9 / 0 | 1 / 10 | 1 / 1 |

서로 다른 사용자 검증은 응답의 store/user, 실제 ZSet 멤버 전체, 중복 없는 저장 점수 1~10,
실제 rank와 응답의 일치, sequence 카운터, TTL, 성공 건수만큼의 metrics 증가를 확인한다.
같은 사용자 검증은 저장 멤버 1개, 점수·rank·카운터 1, 성공 1건만의 metrics 증가를 확인한다.
별도 테스트로 기존 사용자 뒤에 다른 사용자가 있을 때 재요청이 기존 점수·rank·TTL을 보존하는지,
삭제 후 새 번호로 재등록되는지, 가게 비활성 오류 코드가 보존되는지도 확인한다.

| 실행 | 검증 수 | 통과 | 실패 | skip | 근거 |
|---|---:|---:|---:|---:|---|
| 수정 전 | 63 | 21 | 42 | 0 | `baseline.xml` |
| 수정 후 독립 실행 1 | 63 | 63 | 0 | 0 | `fixed-run-1.xml` |
| 수정 후 독립 실행 2 | 63 | 63 | 0 | 0 | `fixed-run-2.xml` |
| 수정 후 독립 실행 3 | 63 | 63 | 0 | 0 | `fixed-run-3.xml` |

수정 전 실패 42개는 서비스 중복 20개, helper 중복 20개, 기존 순서 보존 1개, 업무 오류 보존 1개다.
수정 후에는 각 동시성 시나리오가 총 60배치씩 통과했다. 서로 다른 사용자 600개 요청은 600개 성공,
서비스 중복 600개 요청은 60개 성공·540개 거절, helper 중복 600개 요청도 60개 성공·540개 거절이다.
이 합계는 배치마다 비운 격리 환경의 결과이며 운영에서의 누적 사용자나 처리량이 아니다.

통과: 위 회귀 테스트, Java main/test 컴파일, `queue-service assemble`의 `bootJar`·`jar`, `git diff --check`.
실패: 수정 전 회귀 검증 42개와 최초 Docker API 초기화. 수정 후 회귀 실패는 없다.
미실행: 외부 설정을 요구하는 기존 `QueueServiceApplicationTests.contextLoads`, 필터 없는 전체 `test`/`build`,
다른 서비스의 빌드·테스트, HTTP/API E2E, 운영 환경 부하 검증. 따라서 전체 플랫폼 빌드 통과로 표현하면 안 된다.
기존 helper의 unchecked 연산 및 JVM class sharing 경고는 남아 있다. 별도 lint/checkstyle task는 정의되어 있지 않다.

## 제한적인 현재 소요시간 측정

실행 2·3에서만 start latch 해제 직전부터 모든 Future 수집까지 시간을 추가 기록했다.
동일한 최종 구현·10 worker·20배치 조건이며 warm-up 제외나 이상치 제거를 하지 않았다.
컨테이너 기동·fixture 준비·사후 assert·executor 종료 시간은 제외된다.
서비스 경로는 티켓 검증·분산락·H2 트랜잭션 경계·Redis 왕복·metrics·응답 변환을 포함한다.
helper 경로는 등록과 순번 조회를 포함한다. HTTP 네트워크와 실제 영속 DB 쓰기는 포함하지 않는다.

| 10요청 배치 | 실행 2 최소 / 중앙 / 최대 ms | 실행 3 최소 / 중앙 / 최대 ms |
|---|---:|---:|
| 서로 다른 사용자, 서비스 | 16.120 / 18.911 / 105.268 | 17.389 / 19.896 / 114.339 |
| 같은 사용자, 서비스 | 13.030 / 14.233 / 15.762 | 12.428 / 15.714 / 22.717 |
| 같은 사용자, helper | 0.611 / 0.725 / 1.221 | 0.628 / 0.728 / 1.545 |

수정 전에는 같은 타이머가 없고 실패 assertions/로그 비용도 달라 소요시간 개선율을 계산하지 않았다.
이 짧은 로컬 배치 측정은 지속 처리량·최대 TPS·SLA 근거가 아니다. 2,800 TPS 등의 기존 수치를 확인한 결과도 아니다.

단일 JVM·단일 Redis 조건이다. 다중 서비스 인스턴스, Redis 장애/재시작, 3초 락 lease 만료,
자정 날짜 전환·TTL 경계, 장기 부하·공정성, Redis Cluster는 검증하지 않았다.
현재 설정은 `useSingleServer`이며 Lua의 두 key는 Cluster hash slot을 맞추도록 설계된 키가 아니다.
이 결과를 다른 토폴로지나 운영 전체의 “중복 0 보장”으로 일반화할 수 없다.

## 재실행

저장소 루트에서 Java 17과 실행 중인 Docker가 필요하다. 최초 실행은 Maven/Docker Hub 다운로드가 필요할 수 있다.
macOS 예시이며 다른 환경에서는 JAVA_HOME을 설치된 Java 17 경로로 지정한다.

```bash
JAVA_HOME="$(/usr/libexec/java_home -v 17)" \
JAVA_TOOL_OPTIONS=-Dapi.version=1.44 \
./queue-service/gradlew -p queue-service test \
  --tests '*QueueServiceConcurrencyTest' assemble \
  --rerun-tasks --no-daemon --console=plain
```

`--rerun-tasks`로 Gradle의 UP-TO-DATE 생략을 막는다. 위 명령을 세 번 실행하면 이번 반복 조건과 같다.
각 실행의 XML/HTML은 덮어써지므로 필요하면 실행마다 별도 디렉터리로 복사한다.
기본 결과: `queue-service/build/test-results/test/TEST-com.boeingmerryho.business.queueservice.application.QueueServiceConcurrencyTest.xml` 및 `queue-service/build/reports/tests/test/index.html`.
Docker 접근 실패 시 skip 처리하지 않고 테스트가 실패한다.

수정 전 재현은 현재 작업을 되돌리지 않고 임시 폴더에서 수행할 수 있다.
최종 테스트에는 실행 2부터 추가한 시간 기록만 차이가 있고 회귀 assertions는 동일하다.

```bash
TASK_BASELINE_DIR=$(mktemp -d)
git archive c8b04ddbebd0c5afd72807cb9d0411f65b475ee2 | tar -x -C "$TASK_BASELINE_DIR"
cp queue-service/src/test/java/com/boeingmerryho/business/queueservice/application/QueueServiceConcurrencyTest.java \
  "$TASK_BASELINE_DIR/queue-service/src/test/java/com/boeingmerryho/business/queueservice/application/"
JAVA_HOME="$(/usr/libexec/java_home -v 17)" JAVA_TOOL_OPTIONS=-Dapi.version=1.44 \
  "$TASK_BASELINE_DIR/queue-service/gradlew" -p "$TASK_BASELINE_DIR/queue-service" \
  test --tests '*QueueServiceConcurrencyTest' --no-daemon --console=plain
# 회귀 검증이 실패하는 것이 예상 결과다. 결과 확인 후 임시 폴더는 별도로 정리한다.
```

실행 로그와 XML(머신 사용자명·호스트명·절대 작업 경로만 익명화): [verification/redis-queue-2026-10-04](verification/redis-queue-2026-10-04/).
로그 줄 끝의 공백도 정리했다. 테스트 결과·오류 내용·횟수·시각·소요시간은 변경하지 않았다. XML 시각은 UTC, Gradle 로그의 로컬 표시는 KST다.
테스트 코드의 `testpass`는 매번 폐기하는 로컬 컨테이너용 고정 fixture이며 운영 자격 증명이 아니다.
`python3 docs/verification/redis-queue-2026-10-04/summarize.py`로 보관 XML의 결과와 소요시간을 `summary.json`에 다시 집계할 수 있다.
`source-sha256.txt`는 최종 코드와 테스트를 식별한다.

## 지원서에 사실대로 쓸 수 있는 문장

> 2026년 10월 개인 저장소에서 mock 기반 대기열 테스트를 실제 Redis 통합 테스트로 보완했습니다. 중복 요청의 순번 갱신 결함을 재현하고 원자적 등록으로 수정했으며, 10개 동시 요청 시나리오를 각각 60회 반복해 저장 상태와 성공·거절 응답을 확인했습니다.

이 문장은 이번 보완 작업에 관한 것이다. 과거 프로젝트 당시 성과로 소급하거나 운영 TPS·개선율·전역 중복 방지 보장으로 확장하지 않는다.
