# df-delta-engine-v2 API Contract

Base path:

```text
/api/v1/df/delta
```

Base URL example:

```text
http://localhost:8082/api/v1/df/delta
```

PostgreSQL connection compatibility:

- `DF_DELTA_DB_URL` or `DELTA_PG_URL`
- `DF_DELTA_DB_USERNAME` or `DELTA_PG_USER`
- `DF_DELTA_DB_PASSWORD` or `DELTA_PG_PASSWORD`

This allows `df-delta-engine-v2` to reuse the same PostgreSQL connection
settings that the original `df-delta-engine` used.

## 1. Create Run

`POST /runs`

Request:

```json
{
  "spCode": "KNU10",
  "jobId": "job-20260521-0001",
  "datasetCode": "STDNT_BASIC",
  "requestType": "student-targeted",
  "triggerType": "cron",
  "requestedAt": "2026-05-21T01:00:00+09:00"
}
```

Notes:
- `spCode` is required.
- `jobId`, `datasetCode`, `requestType`, `triggerType`, and `requestedAt` are optional metadata fields.
- The API remains backward compatible with the earlier `{"spCode":"KNU10"}` request body.

Response:

```json
{
  "runId": "1b51d54d-0ebf-406a-8fa0-0f4f72a87fe9",
  "spCode": "KNU10",
  "jobId": "job-20260521-0001",
  "datasetCode": "STDNT_BASIC",
  "requestType": "student-targeted",
  "triggerType": "cron",
  "requestedAt": "2026-05-21T01:00:00+09:00",
  "status": "CREATED",
  "createdAt": "2026-05-19T11:00:00"
}
```

## 2. Register Incoming Records

`POST /runs/{runId}/incoming`

Request:

```json
{
  "spCode": "KNU10",
  "datasetCode": "STDNT_BASIC",
  "records": [
    {
      "KADF_STUDENT_ID": "20250001",
      "KADF_UNIV_CD": "CO00023",
      "KADF_KOR_NM": "홍길동",
      "KADF_DEPT_NM": "컴퓨터공학과"
    },
    {
      "KADF_STUDENT_ID": "20250002",
      "KADF_UNIV_CD": "CO00023",
      "KADF_KOR_NM": "김영희",
      "KADF_DEPT_NM": "전자공학과"
    }
  ]
}
```

Response:

```json
{
  "runId": "1b51d54d-0ebf-406a-8fa0-0f4f72a87fe9",
  "spCode": "KNU10",
  "datasetCode": "STDNT_BASIC",
  "ingestedCount": 2,
  "timestamp": 1779156000000
}
```

Notes:
- `datasetCode` is normalized internally.
- Legacy aliases such as `STUDENT_BASIC`, `GRADE`, and `ENROLLMENT` are accepted and converted.
- `records` must use the standard KADF field names for the target dataset.

## 3. Diff

`GET /runs/{runId}/diff?spCode={spCode}&datasetCode={datasetCode}`

Example:

```text
GET /runs/1b51d54d-0ebf-406a-8fa0-0f4f72a87fe9/diff?spCode=KNU10&datasetCode=STDNT_BASIC
```

Response:

```json
{
  "runId": "1b51d54d-0ebf-406a-8fa0-0f4f72a87fe9",
  "spCode": "KNU10",
  "datasetCode": "STDNT_BASIC",
  "newRecordKeys": [
    "20250001|CO00023"
  ],
  "changedRecordKeys": [
    "20250002|CO00023"
  ],
  "revokedRecordKeys": [
    "20249999|CO00023"
  ]
}
```

## 4. Commit

`POST /runs/{runId}/commit`

Request:

```json
{
  "spCode": "KNU10",
  "datasetCode": "STDNT_BASIC"
}
```

Response:

```json
{
  "runId": "1b51d54d-0ebf-406a-8fa0-0f4f72a87fe9",
  "spCode": "KNU10",
  "datasetCode": "STDNT_BASIC",
  "newCount": 1,
  "changedCount": 1,
  "revokedCount": 1,
  "committedAt": 1779156001000
}
```

Notes:
- Commit recalculates diff against the current incoming/store state.
- `new` and `changed` become store upserts.
- `revoked` becomes store deletes.

## 5. Cleanup Incoming

`DELETE /runs/{runId}/incoming`

Response:
- `204 No Content`

Use this after commit, or after a no-change diff, to clear temporary incoming data.

## 6. Health Check

`GET /health/db`

Response:

```json
{
  "status": "UP",
  "db": "postgres",
  "check": 1
}
```

## Dataset Input Rules

Supported normalized dataset codes:

- `STDNT_BASIC`
- `GRADE_RESULT`
- `ENROLL_INFO`
- `COURSE_INFO`

Accepted legacy aliases:

- `STUDENT`, `STUDENT_BASIC` -> `STDNT_BASIC`
- `GRADE` -> `GRADE_RESULT`
- `ENROLLMENT` -> `ENROLL_INFO`
- `COURSE` -> `COURSE_INFO`

## Recommended NiFi Usage

For each dataset chunk:

1. Create run once per `spCode`
2. Send `incoming` per dataset chunk result from `univ-openapi`
3. Call `diff`
4. If `new/changed/revoked` exists, deliver changed records to SP
5. Call `commit`
6. Call `cleanup`
