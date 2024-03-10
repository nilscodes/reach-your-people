#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Usage: 4-create-tokens.sh TOKEN_NAME TOKEN_COUNT"
    exit 2
fi

TOKEN_NAME=$1
TOKEN_NAME_HEX=$(echo -n $TOKEN_NAME | xxd -ps | tr -d '\n')
TOKEN_COUNT=$2
POLICY_ID=$(cat policy/policyID)
ADDRESS=$(cat payment.addr)
NETIDENTIFIER="--testnet-magic 2"

currentTxData=$(cardano-cli query utxo --address $(cat payment.addr) $NETIDENTIFIER | tail -1)
txIn=$(echo $currentTxData | awk '{print $1"#"$2}')
amountToSend=$(echo $currentTxData | awk '{print $3}')

cardano-cli transaction build-raw \
 --fee 0 \
 --tx-in $txIn \
 --tx-out $ADDRESS+$amountToSend+"$TOKEN_COUNT $POLICY_ID.$TOKEN_NAME_HEX" \
 --mint "$TOKEN_COUNT $POLICY_ID.$TOKEN_NAME_HEX" \
 --minting-script-file policy/policy.script \
 --metadata-json-file metadata.json \
 --out-file matx.raw

cardano-cli query protocol-parameters $NETIDENTIFIER --out-file protocol.json
minFee=$(cardano-cli transaction calculate-min-fee --tx-body-file matx.raw $NETIDENTIFIER --protocol-params-file protocol.json --tx-in-count 1 --tx-out-count 1 --witness-count 2 | awk '{print $1}')
remaining=$(expr $amountToSend - $minFee)

cardano-cli transaction build-raw \
 --fee $minFee \
 --tx-in $txIn \
 --tx-out $ADDRESS+$remaining+"$TOKEN_COUNT $POLICY_ID.$TOKEN_NAME_HEX" \
 --mint "$TOKEN_COUNT $POLICY_ID.$TOKEN_NAME_HEX" \
 --minting-script-file policy/policy.script \
 --metadata-json-file metadata.json \
 --out-file matx.raw

cardano-cli transaction sign  \
 $NETIDENTIFIER \
 --signing-key-file payment.skey  \
 --signing-key-file policy/policy.skey  \
 --tx-body-file matx.raw  \
 --out-file matx.signed

cardano-cli transaction submit \
 $NETIDENTIFIER \
 --tx-file matx.signed
