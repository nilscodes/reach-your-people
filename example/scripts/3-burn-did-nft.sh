#!/bin/bash

if [ "$#" -ne 0 ]; then
    echo "Usage: 3-burn-did-nft.sh"
    exit 2
fi

NETIDENTIFIER="--testnet-magic 2"

POLICY_ID=$(cat policy/policyID)
ADDRESS=$(cat payment.addr)
echo "Policy ID used: $POLICY_ID, Address used: $ADDRESS"

currentTxData=$(cardano-cli query utxo --address $(cat payment.addr) $NETIDENTIFIER | tail -1)
txIn=$(echo $currentTxData | awk '{print $1"#"$2}')
amountToSend=$(echo $currentTxData | awk '{print $3}')

cardano-cli transaction build-raw \
 --fee 0 \
 --tx-in $txIn \
 --tx-out $ADDRESS+$amountToSend \
 --mint "-1 $POLICY_ID." \
 --minting-script-file policy/policy.script \
 --out-file matx.raw

cardano-cli query protocol-parameters $NETIDENTIFIER --out-file protocol.json
minFee=$(cardano-cli transaction calculate-min-fee --tx-body-file matx.raw $NETIDENTIFIER --protocol-params-file protocol.json --tx-in-count 1 --tx-out-count 1 --witness-count 2 | awk '{print $1}')
remaining=$(expr $amountToSend - $minFee)

cardano-cli transaction build-raw \
 --fee $minFee \
 --tx-in $txIn \
 --tx-out $ADDRESS+$remaining \
 --mint "-1 $POLICY_ID." \
 --minting-script-file policy/policy.script \
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
