CREATE TABLE "tokens"
(
    "id"           BIGSERIAL PRIMARY KEY,
    "name"         VARCHAR   NOT NULL,
    "display_name" VARCHAR   NOT NULL,
    "creator"      BIGINT    NOT NULL,
    "project_id"   BIGINT,
    "create_time"  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "modify_time"  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "claims"
(
    "claim_id"        VARCHAR PRIMARY KEY,
    "points"          BIGINT    NOT NULL,
    "category"        VARCHAR   NOT NULL,
    "account_id"      BIGINT    NOT NULL,
    "token_id"        BIGINT    NOT NULL,
    "claimed"         BOOLEAN   NOT NULL DEFAULT FALSE,
    "project_id"      BIGINT,
    "expiration_time" TIMESTAMP,
    "create_time"     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "claim_time"      TIMESTAMP,
    FOREIGN KEY ("token_id") REFERENCES tokens ("id") ON DELETE RESTRICT
);
