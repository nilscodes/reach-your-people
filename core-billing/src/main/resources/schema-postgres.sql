DROP TABLE IF EXISTS "bills";
DROP TABLE IF EXISTS "order_items";
DROP TABLE IF EXISTS "orders";

CREATE TABLE "bills"
(
    "id"                     SERIAL PRIMARY KEY,
    "account_id"             BIGINT    NOT NULL,
    "channel"                VARCHAR   NOT NULL,
    "currency_id"            INT       NOT NULL,
    "amount_requested"       BIGINT    NOT NULL,
    "order_id"               INT       NOT NULL,
    "create_time"            timestamp NOT NULL,
    "amount_received"        BIGINT,
    "transaction_id"         VARCHAR,
    "payment_processed_time" timestamp NULL,
    UNIQUE ("order_id"),
    UNIQUE("channel", "transaction_id")
);

CREATE TABLE "orders"
(
    "order_id" SERIAL PRIMARY KEY
);


CREATE TABLE "order_items"
(
    "item_id"  BIGSERIAL PRIMARY KEY,
    "order_id" INT     NOT NULL,
    "type"     VARCHAR NOT NULL,
    "amount"   BIGINT  NOT NULL
);

ALTER TABLE "order_items"
    ADD FOREIGN KEY ("order_id") REFERENCES "orders" ("order_id") ON DELETE CASCADE;

ALTER TABLE "bills"
    ADD FOREIGN KEY ("order_id") REFERENCES "orders" ("order_id") ON DELETE CASCADE;
