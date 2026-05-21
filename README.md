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
