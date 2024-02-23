#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: 2-create-did-nft.sh IPFS_HASH"
    exit 2
fi

IPFS_HASH=$1
NETIDENTIFIER="--testnet-magic 2"

rm -rf policy
mkdir policy
cardano-cli address key-gen \
    --verification-key-file policy/policy.vkey \
    --signing-key-file policy/policy.skey

echo "{" > policy/policy.script
echo "  \"keyHash\": \"$(cardano-cli address key-hash --payment-verification-key-file policy/policy.vkey)\"," >> policy/policy.script
echo "  \"type\": \"sig\"" >> policy/policy.script
echo "}" >> policy/policy.script

cardano-cli transaction policyid --script-file ./policy/policy.script > policy/policyID

POLICY_ID=$(cat policy/policyID)
ADDRESS=$(cat payment.addr)
echo "Policy ID generated: $POLICY_ID"

echo "{ \"725\": {\"version\": \"1.0\", \"$POLICY_ID\": {\"type\": \"Ed25519VerificationKey2020\", \"files\": [{\"src\": \"ipfs://$IPFS_HASH\", \"name\": \"CIP-0066_NMKR_IAMX\", \"mediaType\": \"application/ld+json\"}], \"@context\": \"https://github.com/IAMXID/did-method-iamx\"}}}" > metadata.json

currentTxData=$(cardano-cli query utxo --address $(cat payment.addr) $NETIDENTIFIER | tail -1)
txIn=$(echo $currentTxData | awk '{print $1"#"$2}')
amountToSend=$(echo $currentTxData | awk '{print $3}')

cardano-cli transaction build-raw \
 --fee 0 \
 --tx-in $txIn \
 --tx-out $ADDRESS+$amountToSend+"1 $POLICY_ID." \
 --mint "1 $POLICY_ID." \
 --minting-script-file policy/policy.script \
 --metadata-json-file metadata.json \
 --out-file matx.raw

cardano-cli query protocol-parameters $NETIDENTIFIER --out-file protocol.json
minFee=$(cardano-cli transaction calculate-min-fee --tx-body-file matx.raw $NETIDENTIFIER --protocol-params-file protocol.json --tx-in-count 1 --tx-out-count 1 --witness-count 2 | awk '{print $1}')
remaining=$(expr $amountToSend - $minFee)

cardano-cli transaction build-raw \
 --fee $minFee \
 --tx-in $txIn \
 --tx-out $ADDRESS+$remaining+"1 $POLICY_ID." \
 --mint "1 $POLICY_ID." \
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
