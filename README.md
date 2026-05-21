# df-delta-engine-v2

Standalone delta engine extracted from `df-federation` for v2 orchestration.

## Scope

- Keeps only delta run / incoming / diff / commit / cleanup responsibilities
- Removes the `df-common` project dependency by embedding the minimum shared classes
- Normalizes legacy dataset codes such as `STUDENT_BASIC`, `GRADE`, and `ENROLLMENT`
- Designed to work behind `nifi-idp`, while `univ-openapi` handles provider queries

## PostgreSQL Compatibility

`df-delta-engine-v2` is intentionally compatible with the original delta engine's
PostgreSQL environment variables.

That means you can point `v2` at the same PostgreSQL infrastructure that the
original `df-delta-engine` used.

Supported environment variable chain:

- `DF_DELTA_DB_URL`
- `DF_DELTA_DB_USERNAME`
- `DF_DELTA_DB_PASSWORD`

Fallback compatibility with original names:

- `DELTA_PG_URL`
- `DELTA_PG_USER`
- `DELTA_PG_PASSWORD`

Default local fallback:

- `jdbc:postgresql://localhost:5432/df_delta`
- `df_delta_user`
- `df_delta_password`

## Build

Maven:

```bash
/opt/homebrew/bin/mvn test
```

Gradle:

```bash
/opt/homebrew/bin/gradle test
```

Note:
- Maven is the current verification path in this workspace.
- Gradle 9.5.1 may require a Spring Boot / wrapper version alignment before local execution.

## Docker Compose

`df-delta-engine-v2`는 Docker Compose로도 바로 띄울 수 있다.

이 구성은 아래를 전제로 한다.

- `delta-postgres`는 같은 compose 안에서 같이 띄운다
- `df-delta-engine-v2`만 `nifi-idp`와 같은 Docker network에 붙인다
- 따라서 NiFi 컨테이너는 `http://df-delta-engine-v2:8082/api/v1/df/delta` 로 호출할 수 있다
- 호스트에서는 기본적으로 `127.0.0.1:18089` 로만 접근한다

### 1. 환경파일 준비

```bash
cp .env.sample .env
```

`DELTA_DOCKER_NETWORK` 값은 **NiFi-IdP가 이미 붙어 있는 실제 Docker network 이름**으로 맞춰야 한다.

확인 예시:

```bash
docker network ls
```

보통은 아래처럼 보일 수 있다.

```text
nifi-idp_nifi-net
```

### 2. Postgres schema 선행 생성

현재 v1 운영 방식은 `DF_DELTA_JPA_DDL_AUTO=validate` 기준이므로,
아래 테이블은 미리 만들어둬야 한다.

- `delta_runs`
- `delta_record_incoming`
- `delta_record_store`

### 3. 실행

```bash
docker compose up --build -d
```

### 4. 상태 확인

```bash
docker compose ps
curl http://127.0.0.1:18089/api/v1/df/delta/health/db
```

### 5. NiFi에서 호출할 주소

같은 Docker network 안의 `nifi-idp` 컨테이너에서는:

```text
http://df-delta-engine-v2:8082/api/v1/df/delta
```

호스트에서 직접 확인할 때는:

```text
http://127.0.0.1:18089/api/v1/df/delta
```

## Run With Existing Delta PostgreSQL

If you want to reuse the same PostgreSQL settings as the original delta engine,
the simplest way is to export the original variables as-is:

```bash
export DELTA_PG_URL='jdbc:postgresql://<host>:5432/<db>'
export DELTA_PG_USER='<user>'
export DELTA_PG_PASSWORD='<password>'

/opt/homebrew/bin/mvn -Dmaven.repo.local=/Users/nayeong/Developer/data-federation/df-delta-engine-v2/.m2 spring-boot:run
```

Or use the explicit v2 names:

```bash
export DF_DELTA_DB_URL='jdbc:postgresql://<host>:5432/<db>'
export DF_DELTA_DB_USERNAME='<user>'
export DF_DELTA_DB_PASSWORD='<password>'

/opt/homebrew/bin/mvn -Dmaven.repo.local=/Users/nayeong/Developer/data-federation/df-delta-engine-v2/.m2 spring-boot:run
```

## Important Operational Note

Reusing the same PostgreSQL server is fine.

Reusing the same database and tables is also possible if the original delta flow
is no longer actively writing to them at the same time.

If the original delta engine and `v2` both write to the same `runs / incoming /
snapshot(store)` tables concurrently, state can collide.
