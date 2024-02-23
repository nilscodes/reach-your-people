#!/bin/bash

if [ "$#" -ne 0 ]; then
    echo "Usage: 1-create-address.sh"
    exit 2
fi

NETIDENTIFIER="--testnet-magic 2"

cardano-cli address key-gen --verification-key-file payment.vkey --signing-key-file payment.skey
cardano-cli address build --payment-verification-key-file payment.vkey --out-file payment.addr $NETIDENTIFIER