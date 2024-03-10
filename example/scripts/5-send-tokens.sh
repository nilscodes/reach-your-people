#!/bin/bash

if [ "$#" -ne 3 ]; then
    echo "Usage: 5-send-tokens.sh TARGET_ADDRESS TOKEN_COUNT TOKEN_NAME"
    exit 2
fi

TARGET_ADDRESS=$1
TOKEN_COUNT=$2
TOKEN_NAME=$3
TOKEN_NAME_HEX=$(echo -n $TOKEN_NAME | xxd -ps | tr -d '\n')
POLICY_ID=$(cat policy/policyID)
ADDRESS=$(cat payment.addr)
NETIDENTIFIER="--testnet-magic 2"

currentTxData=$(cardano-cli query utxo --address $(cat payment.addr) $NETIDENTIFIER | tail -1)
txIn=$(echo $currentTxData | awk '{print $1"#"$2}')
lovelaceInWallet=$(echo $currentTxData | awk '{print $3}')
minUtxoToSend=1500000
remainingInWallet=$(expr $lovelaceInWallet - $minUtxoToSend)
currentTokenCount=$(echo $currentTxData | awk '{print $6}')
currentTokenRemaining=$(expr $currentTokenCount - $TOKEN_COUNT)

cardano-cli transaction build-raw \
 --fee 0 \
 --tx-in $txIn \
 --tx-out $ADDRESS+$remainingInWallet+"$currentTokenRemaining $POLICY_ID.$TOKEN_NAME_HEX" \
  --tx-out $TARGET_ADDRESS+$minUtxoToSend+"$TOKEN_COUNT $POLICY_ID.$TOKEN_NAME_HEX" \
 --out-file matx.raw

cardano-cli query protocol-parameters $NETIDENTIFIER --out-file protocol.json
minFee=$(cardano-cli transaction calculate-min-fee --tx-body-file matx.raw $NETIDENTIFIER --protocol-params-file protocol.json --tx-in-count 1 --tx-out-count 1 --witness-count 1 | awk '{print $1}')
remaining=$(expr $remainingInWallet - $minFee)

cardano-cli transaction build-raw \
 --fee $minFee \
 --tx-in $txIn \
 --tx-out $ADDRESS+$remaining+"$currentTokenRemaining $POLICY_ID.$TOKEN_NAME_HEX" \
 --tx-out $TARGET_ADDRESS+$minUtxoToSend+"$TOKEN_COUNT $POLICY_ID.$TOKEN_NAME_HEX" \
 --out-file matx.raw

cardano-cli transaction sign  \
 $NETIDENTIFIER \
 --signing-key-file payment.skey  \
 --tx-body-file matx.raw  \
 --out-file matx.signed

cardano-cli transaction submit \
 $NETIDENTIFIER \
 --tx-file matx.signed
