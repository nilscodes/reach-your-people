DROP TABLE IF EXISTS "project_notification_settings";
DROP TABLE IF EXISTS "linked_external_accounts";
DROP TABLE IF EXISTS "subscriptions";
DROP TABLE IF EXISTS "project_tags";
DROP TABLE IF EXISTS "project_policies";
DROP TABLE IF EXISTS "project_roles";
DROP TABLE IF EXISTS "project_stakepools";
DROP TABLE IF EXISTS "project_dreps";
DROP TABLE IF EXISTS "projects";
DROP TABLE IF EXISTS "external_accounts";
DROP TABLE IF EXISTS "account_settings";
DROP TABLE IF EXISTS "accounts";

CREATE TABLE "accounts"
(
    "account_id"       BIGSERIAL PRIMARY KEY,
    "display_name"     varchar(200) NOT NULL,
    "create_time"      timestamp    NOT NULL,
    "premium_until"    timestamp    NULL,
    "cardano_settings" BIT(16)      NOT NULL DEFAULT B'1111111111111111'
);

CREATE TABLE "account_settings"
(
    "account_id"    bigint,
    "setting_name"  varchar(64)   NOT NULL,
    "setting_value" varchar(4096) NOT NULL,
    UNIQUE ("account_id", "setting_name")
);

CREATE TABLE "linked_external_accounts"
(
    "link_id"             BIGSERIAL PRIMARY KEY,
    "account_id"          BIGINT    NOT NULL,
    "link_time"           timestamp NOT NULL,
    "role"                smallint  NOT NULL,
    "external_account_id" BIGINT    NOT NULL,
    "settings"            BIT(16)   NOT NULL DEFAULT B'1111111111111111',
    "last_confirmed"      timestamp,
    "last_tested"         timestamp,
    UNIQUE ("account_id", "role", "external_account_id")
);

CREATE TABLE "subscriptions"
(
    "account_id" BIGINT   NOT NULL,
    "project_id" INT      NOT NULL,
    "status"     SMALLINT NOT NULL
);

CREATE TABLE "external_accounts"
(
    "external_account_id"     BIGSERIAL PRIMARY KEY,
    "external_reference_id"   varchar(200) NOT NULL,
    "external_reference_name" varchar(200),
    "display_name"            varchar(200),
    "registration_time"       timestamp,
    "unsubscribe_time"        timestamp,
    "account_type"            varchar(32)  NOT NULL,
    "metadata"                bytea NULL
);

CREATE TABLE "projects"
(
    "project_id"          SERIAL PRIMARY KEY,
    "name"                varchar(255),
    "logo"                text,
    "url"                 varchar(255),
    "description"         text,
    "registration_time"   timestamp NOT NULL,
    "category"            INTEGER   NOT NULL,
    "manually_verified"   timestamp NULL,
    "verification_reason" text      NULL
);

CREATE TABLE "project_notification_settings"
(
    "notification_settings_id" BIGSERIAL PRIMARY KEY,
    "project_id"               INT       NOT NULL,
    "external_account_link_id" BIGINT    NOT NULL,
    "create_time"              timestamp NOT NULL,
    UNIQUE ("project_id", "external_account_link_id")
);

CREATE TABLE "project_tags"
(
    "project_id" INT          NOT NULL,
    "tag"        varchar(200) NOT NULL
);

CREATE TABLE "project_policies"
(
    "project_id"          INT          NOT NULL,
    "name"                varchar(200) NOT NULL,
    "policy_id"           varchar(56)  NOT NULL,
    "manually_verified"   timestamp    NULL,
    "verification_reason" text         NULL
);

CREATE TABLE "project_stakepools"
(
    "project_id"         INT         NOT NULL,
    "pool_hash"          varchar(56) NOT NULL,
    "verification_nonce" varchar(64) NOT NULL,
    "verification_time"  timestamp   NOT NULL
);

CREATE TABLE "project_dreps"
(
    "project_id"         INT         NOT NULL,
    "drep_id"            varchar(56) NOT NULL,
    "verification_nonce" varchar(64) NOT NULL,
    "verification_time"  timestamp   NOT NULL
);

CREATE TABLE "project_roles"
(
    "project_id" INT      NOT NULL,
    "role"       smallint NOT NULL,
    "account_id" BIGINT   NOT NULL
);

ALTER TABLE "account_settings"
    ADD FOREIGN KEY ("account_id") REFERENCES "accounts" ("account_id") ON DELETE CASCADE;

ALTER TABLE "project_tags"
    ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE CASCADE;

ALTER TABLE "project_policies"
    ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE CASCADE;

ALTER TABLE "project_stakepools"
    ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE CASCADE;

ALTER TABLE "project_dreps"
    ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE CASCADE;

ALTER TABLE "project_roles"
    ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE CASCADE;

ALTER TABLE "project_roles"
    ADD FOREIGN KEY ("account_id") REFERENCES "accounts" ("account_id") ON DELETE CASCADE;

ALTER TABLE "project_notification_settings"
    ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE CASCADE;

ALTER TABLE "project_notification_settings"
    ADD FOREIGN KEY ("external_account_link_id") REFERENCES "linked_external_accounts" ("link_id") ON DELETE CASCADE;

ALTER TABLE "linked_external_accounts"
    ADD FOREIGN KEY ("account_id") REFERENCES "accounts" ("account_id") ON DELETE CASCADE;

ALTER TABLE "linked_external_accounts"
    ADD FOREIGN KEY ("external_account_id") REFERENCES "external_accounts" ("external_account_id") ON DELETE CASCADE;

ALTER TABLE "subscriptions"
    ADD FOREIGN KEY ("account_id") REFERENCES "accounts" ("account_id") ON DELETE CASCADE;

ALTER TABLE "subscriptions"
    ADD FOREIGN KEY ("project_id") REFERENCES "projects" ("project_id") ON DELETE CASCADE;
