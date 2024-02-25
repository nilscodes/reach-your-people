package io.vibrantnet.ryp.core.verification.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

const val DEFAULT_USERNAME = "username"
const val DEFAULT_DISPLAY_NAME = "Display Name"
const val DEFAULT_REFERENCE_ID = "123"
const val PAYLOAD = """{"payload":{"date":1708486681121,"policyID":"d390811dc0e73d62d66eec7616a01d7951c286219e19e789c0d87d3e","accounts":["twitter",{"twitterId":"1111111111111111111","username":"somebodycodes"},"discord",{"discordId":"222222222222222222","username":"somebodycodes"},"github",{"githubId":"3333333","username":"somebodycodes","displayName":"Somebody Codes"},"twitch",{"twitchId":"44444444"},"google",{"googleId":"5555555","username":"somebodycodes","displayName":"Somebody Codes"},"linkedin",{"linkedinId":"NAR-HARF","username":"somebodycodes","displayName":"Somebody Codes"},"apple",{"appleId":"77777777"},"steam",{"displayName":"Somebody Codes"},"unknown",{"unknownId":"88888888"}],"version":"1.0.0"},"@context":"https://github.com/IAMXID/did-method-iamx","signatures":[{"signature":"cuHeyrjCEHZay6KVAnkbVoSPo8fdXXsHTxq6EfiBnQHxv1Ash84fwfCdhx6KFZK4DF4UBGGJyitM9sPJoDdhUfrWeTdt6QLdykpnxSV6P7trAsVyaoY4wMUzm44y7qdCxLmtdc5tVRBbGqPxjNfcVADSJscXrRq4HDABidiP6y4W5ZqV1aHr69bSVgXZYm6pacafmRKbvdD88Yqyn9F7QHHYvLmCFCoTA4eqQfQWrkR6w8VFCvk8VHA3jS1imCD9QhHGPhQV5mz7Xo8RqqmKKg49r5f9jKFZcp3LPxAyvey2xEKbi8DCmTghBFnTpx59k9qchp3V1PmLvLJZXx7Xh6ng8MYN8Fx5bcH19oPFxorGRX8MLK2EdJsU6SKXNfSePuy5yGPenyR8XxuQQ3pRP9mgFVzPtMpipF4BkPdHT2rWH653kKPeB4uvGYRVbRZknYw994Jr15vJrVzMpDxsaasrXeXQm5Wb6xtpoMoncSroCgfySZRSSKgHVxN8CUYinVN1L1oiVWuzHsSsEmTj8nJLhC3bbbGCZhhSuwEoFT9uP2gTGQzjo2YWfkXYKvpmJahVkQKYqVw58LsrcL4CTuzvq4tn7fhVxcRpM1skutSJ4xszYXQt6AKp5Rp9VXoLNi4fF3T7KDpNed7ijAAdxUz9xpnJNDb4qchXXuebZ7gdCyTWpcDJJE7FP1LRViGDd9ZbvZ3gPyHQ91UUxMsFm46LU6Hf1zp8hhfiX17dtoZuswyJYMFAS8S1YF6E64gZT6eM8iAKY4uEY1k8KGd5zcNcLHp3W9Muhi5DCfZQNwSB18J9f8JkoHjjLcWKeS8W2pHTzt8HJHMZGNaMfcEmQwwUzUs3nvysGQey1WXiyLpX8EjYDMBcjqdBQJB78CjqJ8MJGAyTFTe6htTMv7cirMXr26c3Km2esNHXrU","DID":[["did:iamx:cardano:zggW2SuC7Phxth3SAjhtz7YuNfrcDhoRTz5WrSZ2xh38BTw","fXpi6R1vn5PzPFkBb6nJ9v3vq4bFFbixMRnAvexFbesEe2Ek1Q5fgWRAnJjVvYMc","UqReB2vbSELMZd586CnbUqcZ1A9HRr1GXQZtoy5g6UmwN73Vmvck2cioUPLUAxFV","Ddo85J5a2BECMGx6NuHCDNM6bz69CrZr6u33wnr4Ct1ZFkXyKypo9oD7Nrk16Jq6","ayoB1EFyy1nUtg8oVxCLnxevfRh7mY7vyF5QEjwMMrvSU2sBmQzCciNFmZVSSDZT","o99n79nDfBV3fvmyQyPrtscwsHqex1sbyTNRHyuG8hPJBqnFZFViiZXfyBSKb2kb","aznC6EPESg7nPq6JASfFvQLgCRaBMsU4H2ukRKcvSHry1bb8EmHpPuwCb62dqJJp","TzKBWSSdtiL8c3Zr4k5Kae3j5AUsyGk9cnSzf2c6wNgFddPr28RsY3pyPhHuRYQW","nrdxN2Hv184qwVmhw3Sb9VcGBNmH6yaKSnGMREaLyandgK2SrenouHGXpmsSUHzL","uEr6iE1qVCk33oE7E9GNSQhc3GuGJ6dauk5G64cxPYYWUQEtFWZS3TDgFE8ToKtJ","AvZghmeeguVS7qzuLASMyCe2dYueZBgKH9QG7nc67CJiLZpivT9LWDAcfSJ2JVB9","4REfcZ2bSvLoUuYTvg73TaqFr65x5Z51qJbP8wQ9xgXAKwCAgUgzDzocmvNzTecT","PW5z2fBj89E5tuAFoedfjiM6F4HHmG23kTwWLs1hw6Jbtqcg37zqAawFnNNfUpm3","Awfxp55KSgw3cKXn361gPdSbQcDuvifrCcTGdrLTeLbg9giSHa2RSsd4FYggoK9N","xxLrFLKQfq8Qo9TBG17T1Eo8XXXA9ZUWcQ9je3y2tMQNtvhFGYSxGzfX1n3uuJAY","TG1gSu1Ls1B42roPsPCWhzpRFaVzJ2iAMWEzzytttU1apCxd6Dzp5NHMZgXUaBfe","4"]],"description":"Issuer","storageLocation":{"uri":"ipfs://QmQMgaqppjV41TmciCW6Va8JPAmwqPZFaYHTJWSLPnEZR6","type":"ipfs"}},{"signature":"eKgdkDv3cVGJbjSeBJcfSttuQqdurLZ3nxTtbxPCBhoW8JoxE9DufEP2GZQVfgJJHXYBtoN2hWJxcuH7k615d4u4L8obVDSWpSv8x1XmhxB25jdqgvzWFb2fiTMcfcK4Lig3x2EUFrhXH8PrCHZwZynmnUHeteWkkY8ADrAsXExBncWmEeo4mjKzmpKUdLSZPeFhgHiJJmMK2V3JVGr6QKBcjt79fYGnAvcCmVjzL2ufLnYXtKSBxVkyMnbSR2Ws8xniQwghFzstyBJkuYyTQNCYtE17pTPNUKKcHHUvuATytYQwee44ATFgsu1fuQNGaqGx1mdWJyhphryh3Pqro6Fm2U3mCnQnQ81e5VqkKhrLoo14JeWgXzF7BinBprZep3rPtm2DmYzvtB2oD95JwJbLV5EqGscJ1FzZb9QDhcJPFYS1meKoaUyC8PSF9r9DUQhxMc9f7NYAAPY6ddU9J5EcEZ6osoUSUJTY9bHXFtLYzxJtFsgM4JGiRvDM5LhvTMjgo3jMVPZsZ3XwP1gQYpw1T5yRvw6eGLwhr8DPZf2xYRhyQwveWdNz8zNCbTb4XKs5Gj4Fp2QRRw1B4sJtN6d8iSKEVXeYCzq5pQVToZVaiu9uRKpXvQbUgqiPGCPSo5XHZ3ZmatqMMhu6uHzA6Aiy5rxDbA3kWrcVP47rjFCNPkA3XpGB4EESnq7owL1FuyHeQXEDjkT4YyzJpeBiFAX1fBvCEWnSfBZFBXs8CVJWsPX1qArWiZSuvwC2UtCzGuGKQ6N6AdiNT2LFQg3hVifM3xrHfB6zmNoUDUtnivonRL2FxDxdLFxf6XPzQyjCdjE1pCgxp8VmPMJmjUnRKUjRaAnyviH5BWCz1ydFCrUjMMdgRPg57JfJgthiUD44nat7LjRRyhLpWxBbNowwDXGWzBz1dXbDz897Tr","DID":[["did:iamx:cardano:zggW2SuC7Phxth3SAjhtz7YuNfrcDhoRTz5WrSZ2xh38BTw","fXpi6R1vn5PzPFkBa48Ts5AjKfN7rv1XzSoG93PP46DbhrvQgrxeJVPJnE5zH7ZK","eSsBpx3J8qsFhwQMVLHNKfw3J3zPNuCQ9eAVtMiL1PJLdfWahW9cSWMPaLXyyyPB","H7oFC7HgjDcXkc2g7mnW2M2qp9Pz5iawLjWBrhfnQPBspTmfyttFJ4p8WJcVnd4x","hmtCm3ZMoGMWchMe3YojaazHKz6rVdk5eM6THH929oMb7kUT7USJgvTyavMCdjam","ksjKWn13omUAVu7LyPeo1cX1sWMf9x1t7EXr9yaBGrdV2aCaCpnaxUY71KEp6hhD","YiJoGk1LzDaos9RuDLvTEorQ2JSxSJHhCKSPLyye6w3rjRQ4gCTVH2N9ZuBqVPp6","UCjFjwiMBq5FS7MKnoYt7WoZwsUDGZPDwxAzATz8jSKd1SYcyYqeJtZYCRvcAZBe","EEgbAFrQwXJgDP3bvxWjvKy33DauoPabyC3WerA2HmH11Mtra4M458jpaJn5Rpva","BwiBuMQS4VvJa36qFVLbrZDMrUmgUUTuafCJTwpMPqcPm8G1T7MRoUpLwHo6vXA5","6hrmdcM1hKUEyHyNzjQEouW4J9erHvB5WYZP5yfkh3XeX1M5TxQMakPUzMGFwNsz","zpf6YrkYDS4NSRvAuL83HgUSibSqz1MNrJbougXEcWnY1y5g2gfa2u6yd6LQ2ZaT","PSAWfee3CesmzhBZEj2MEQYLdgNCr9nPWGdz6BDVZB2dfjg42EaDmEk3SbTqT9TS","ioQPrEYZgw1Z35BCT6U969ALBtUTeHpJrpcK7gSMjH4DcQ88EsBFFQuABroN5gJh","n4ZpwfJaKTMjUZZYVnmNY6emLVsmcMQmfPxv1qxhyYU7y7F2EGWGQtgiZE9Cndwe","5h1ULSVFhoyLV5cHkDz18RkETQ9DKqp1aLqNbDgwGtVGvJJjbRn4T4y2eJSNbGmp","G"]],"description":"Creator","storageLocation":{"uri":"ipfs://Qmb2uayBDYXquE8DAP99Jpukx7WoxvYNyjMkaTJ3Lwd1tG","type":"ipfs"}},{"signature":"Wtg7pRt7iZaJnsHNhgruy9pu9oGtLTVT3U8Yznf8JPqzr9FMT9NxweCedo5GcqhSo62eK452gtRUziY3kXuJN31A1Umqm8aq1LegysZNSenHmsY2zni3fdcJuURfYoyfxqRf6h21UKxek6kzbNYHSN6GdhaRS71MiDQaGYCiS2kcRdYYjTo8QkTYU6fY52Akx2fjrbRAjzmPCSHYzsyjCqAMKPgk7TpJxdt9RkPPV4WEq1WMngjC7nYJXYFFYhRbQe8aaC1pxsoYh9tjmTnkKKcZGuALNxou6hgE7Qgsn64zTdtvQqTy49z5itbvtfLscPwi8Y6pawH2fwX52kkFZQugaYuVLt8GyQBkSB16jy79vEb1CFtTa5w9CvTXzRENo6vj9v6ESuCcoebeVNskCboEgQoqHrEzbYsRTPw9ViVxb4pYwAwMzyExmsiohJxpc846ChEUmp7pnLMjp8TaUK7nScFVNagMoNHTUH7uuDyaqHPzyDGM9rz5w9uh3Kgg9QCwviwUpzcfz3xUJgPXmobFbxwQ17D1qGirXazXHBTLefhDVoyB2fz1oBbgnUoM5Q9WaWGCY9nTKKVKoTkLC8dap1XYECss6ghPQdnohY7mBFuLF23zSF1HzpBfYMDQXEFLdNRMefo7Drj1KE1YuopFpLasmTJEnvf8aHcNmX675iCUjZhbDjynfuEMEfky3hf2tu5AGEwWVBjMc6cAYYkqmKVdqcYiA84rgSXSJxho6bj1zeTgc2foX5nx5aktoR2AwDQfxcHQMRur5QTshFjSCcTB4j6tsMQSchvGz4Zhweu3MRp1AWApG8DMnxmQ4qAWmq1qs6MUhgDTJLMn8FRXzaWZ9Hiyr2pCj4nDMtt6a8G9RFpE92ZsUTXdZzhTZRvNK1HrsPHDD94NSG9a5kqGYEnrAcr7r3eV4Q","DID":[["did:iamx:cardano:zggW2SuC7Phxth3SAjhtz7YuNfrcDhoRTz5WrSZ2xh38BTw","fXpi6R1vn5PzPFkBZzQ3jvVAcFD5FK1nXLz7kLJn1cFEPFDUecSKaRNLsXBh5G6v","o8J2y5gBSVZG7pfwfvZVPvHLpSP2VQTmRDb34QfNGLsR8Pp2vRuhPDDs8REEtdZp","bcgy43XcHMqDtSdXoiFzPeAnSLWuaggTHJcDtAbD1Uc52hVqsHrTsjote441mCYk","SiUNdXDsVincFs2pTW7V3JRYnMrewb1GNtmnPLqSCzwkPkqDHuSYeprPZnHfC7Aj","svpWbueQHPT4gvUMz7XQ9xsUR5dPbQ4SMMf2teZ6DGiNFuc52Qo3SorohMdeLnp6","3uMuW4P8bnFqriwUH8JZvNNqPCwuDuJZ5YahSbqd6TNATLCutAGmJh5cnxr6RrWm","zNSmkBaPHcaVCHtLyAUiV1D7e1jzSsicYdk9P66ER4WezLQwyMSHvdT8Ww4iqC5L","PpGme2cPyAzEFTGp4hBK91pRbJfmY337GkGTGHLpvmg1TedvW3UxgUZap7uMNFiw","L5SYztY4K2xk7YmpXHZUwQGrRoDahGewEyPADJBK5hL3A863SccoUAeNBJhNhymB","83pEM2CgQVqqyHUZprHsZH2vZh4rpy8i1z84orfbjsfGfJSuGXHjxoJQPPCMYYpS","X8jjKNi7Z43vMqCYd4L97hkqg7wStYPeTHKii2gbVCzXHqxnMcrnrgUVk1fMhsJR","cUkcdrrGjpEDFMctE9ec4LcvJQbbxFULJCjxmYqeMEAxVVYoAwdp6cazQ3Bftety","4TSY2TRJnKDNqV9UR31mpTVFgY568GTLDYSMcvrXA13ACdztSL6dDuCZB67i9YuP","LVxnzFHwXdbN1nFV2oW3riAELL1WvACZoMq71J6GQEXKtWJKpgrREpgquQHNkyde","HcPiybAUMHFkZZ71rPXKJrra1CXAZS3E28zZiDuH6QHqZTPVjvXmAqbJCnJPuXma","c"]],"description":"IAMX","storageLocation":{"uri":"ipfs://QmZCDX15cMdUQa8apRM7UAu6HMxDe7r42LVcCUp95HfXue","type":"ipfs"}}]}"""
const val PAYLOAD_WITH_POLICYID_OBJECT = """{"payload":{"date":1708486681121,"policyID":{},"accounts":["twitter",{"twitterId":"1111111111111111111","username":"somebodycodes"},"discord",{"discordId":"222222222222222222","username":"somebodycodes"},"github",{"githubId":"3333333","username":"somebodycodes","displayName":"Somebody Codes"},"twitch",{"twitchId":"44444444"},"google",{"googleId":"5555555","username":"somebodycodes","displayName":"Somebody Codes"},"linkedin",{"linkedinId":"NAR-HARF","username":"somebodycodes","displayName":"Somebody Codes"},"apple",{"appleId":"77777777"},"steam",{"displayName":"Somebody Codes"},"unknown",{"unknownId":"88888888"}],"version":"1.0.0"},"@context":"https://github.com/IAMXID/did-method-iamx","signatures":[{"signature":"cuHeyrjCEHZay6KVAnkbVoSPo8fdXXsHTxq6EfiBnQHxv1Ash84fwfCdhx6KFZK4DF4UBGGJyitM9sPJoDdhUfrWeTdt6QLdykpnxSV6P7trAsVyaoY4wMUzm44y7qdCxLmtdc5tVRBbGqPxjNfcVADSJscXrRq4HDABidiP6y4W5ZqV1aHr69bSVgXZYm6pacafmRKbvdD88Yqyn9F7QHHYvLmCFCoTA4eqQfQWrkR6w8VFCvk8VHA3jS1imCD9QhHGPhQV5mz7Xo8RqqmKKg49r5f9jKFZcp3LPxAyvey2xEKbi8DCmTghBFnTpx59k9qchp3V1PmLvLJZXx7Xh6ng8MYN8Fx5bcH19oPFxorGRX8MLK2EdJsU6SKXNfSePuy5yGPenyR8XxuQQ3pRP9mgFVzPtMpipF4BkPdHT2rWH653kKPeB4uvGYRVbRZknYw994Jr15vJrVzMpDxsaasrXeXQm5Wb6xtpoMoncSroCgfySZRSSKgHVxN8CUYinVN1L1oiVWuzHsSsEmTj8nJLhC3bbbGCZhhSuwEoFT9uP2gTGQzjo2YWfkXYKvpmJahVkQKYqVw58LsrcL4CTuzvq4tn7fhVxcRpM1skutSJ4xszYXQt6AKp5Rp9VXoLNi4fF3T7KDpNed7ijAAdxUz9xpnJNDb4qchXXuebZ7gdCyTWpcDJJE7FP1LRViGDd9ZbvZ3gPyHQ91UUxMsFm46LU6Hf1zp8hhfiX17dtoZuswyJYMFAS8S1YF6E64gZT6eM8iAKY4uEY1k8KGd5zcNcLHp3W9Muhi5DCfZQNwSB18J9f8JkoHjjLcWKeS8W2pHTzt8HJHMZGNaMfcEmQwwUzUs3nvysGQey1WXiyLpX8EjYDMBcjqdBQJB78CjqJ8MJGAyTFTe6htTMv7cirMXr26c3Km2esNHXrU","DID":[["did:iamx:cardano:zggW2SuC7Phxth3SAjhtz7YuNfrcDhoRTz5WrSZ2xh38BTw","fXpi6R1vn5PzPFkBb6nJ9v3vq4bFFbixMRnAvexFbesEe2Ek1Q5fgWRAnJjVvYMc","UqReB2vbSELMZd586CnbUqcZ1A9HRr1GXQZtoy5g6UmwN73Vmvck2cioUPLUAxFV","Ddo85J5a2BECMGx6NuHCDNM6bz69CrZr6u33wnr4Ct1ZFkXyKypo9oD7Nrk16Jq6","ayoB1EFyy1nUtg8oVxCLnxevfRh7mY7vyF5QEjwMMrvSU2sBmQzCciNFmZVSSDZT","o99n79nDfBV3fvmyQyPrtscwsHqex1sbyTNRHyuG8hPJBqnFZFViiZXfyBSKb2kb","aznC6EPESg7nPq6JASfFvQLgCRaBMsU4H2ukRKcvSHry1bb8EmHpPuwCb62dqJJp","TzKBWSSdtiL8c3Zr4k5Kae3j5AUsyGk9cnSzf2c6wNgFddPr28RsY3pyPhHuRYQW","nrdxN2Hv184qwVmhw3Sb9VcGBNmH6yaKSnGMREaLyandgK2SrenouHGXpmsSUHzL","uEr6iE1qVCk33oE7E9GNSQhc3GuGJ6dauk5G64cxPYYWUQEtFWZS3TDgFE8ToKtJ","AvZghmeeguVS7qzuLASMyCe2dYueZBgKH9QG7nc67CJiLZpivT9LWDAcfSJ2JVB9","4REfcZ2bSvLoUuYTvg73TaqFr65x5Z51qJbP8wQ9xgXAKwCAgUgzDzocmvNzTecT","PW5z2fBj89E5tuAFoedfjiM6F4HHmG23kTwWLs1hw6Jbtqcg37zqAawFnNNfUpm3","Awfxp55KSgw3cKXn361gPdSbQcDuvifrCcTGdrLTeLbg9giSHa2RSsd4FYggoK9N","xxLrFLKQfq8Qo9TBG17T1Eo8XXXA9ZUWcQ9je3y2tMQNtvhFGYSxGzfX1n3uuJAY","TG1gSu1Ls1B42roPsPCWhzpRFaVzJ2iAMWEzzytttU1apCxd6Dzp5NHMZgXUaBfe","4"]],"description":"Issuer","storageLocation":{"uri":"ipfs://QmQMgaqppjV41TmciCW6Va8JPAmwqPZFaYHTJWSLPnEZR6","type":"ipfs"}},{"signature":"eKgdkDv3cVGJbjSeBJcfSttuQqdurLZ3nxTtbxPCBhoW8JoxE9DufEP2GZQVfgJJHXYBtoN2hWJxcuH7k615d4u4L8obVDSWpSv8x1XmhxB25jdqgvzWFb2fiTMcfcK4Lig3x2EUFrhXH8PrCHZwZynmnUHeteWkkY8ADrAsXExBncWmEeo4mjKzmpKUdLSZPeFhgHiJJmMK2V3JVGr6QKBcjt79fYGnAvcCmVjzL2ufLnYXtKSBxVkyMnbSR2Ws8xniQwghFzstyBJkuYyTQNCYtE17pTPNUKKcHHUvuATytYQwee44ATFgsu1fuQNGaqGx1mdWJyhphryh3Pqro6Fm2U3mCnQnQ81e5VqkKhrLoo14JeWgXzF7BinBprZep3rPtm2DmYzvtB2oD95JwJbLV5EqGscJ1FzZb9QDhcJPFYS1meKoaUyC8PSF9r9DUQhxMc9f7NYAAPY6ddU9J5EcEZ6osoUSUJTY9bHXFtLYzxJtFsgM4JGiRvDM5LhvTMjgo3jMVPZsZ3XwP1gQYpw1T5yRvw6eGLwhr8DPZf2xYRhyQwveWdNz8zNCbTb4XKs5Gj4Fp2QRRw1B4sJtN6d8iSKEVXeYCzq5pQVToZVaiu9uRKpXvQbUgqiPGCPSo5XHZ3ZmatqMMhu6uHzA6Aiy5rxDbA3kWrcVP47rjFCNPkA3XpGB4EESnq7owL1FuyHeQXEDjkT4YyzJpeBiFAX1fBvCEWnSfBZFBXs8CVJWsPX1qArWiZSuvwC2UtCzGuGKQ6N6AdiNT2LFQg3hVifM3xrHfB6zmNoUDUtnivonRL2FxDxdLFxf6XPzQyjCdjE1pCgxp8VmPMJmjUnRKUjRaAnyviH5BWCz1ydFCrUjMMdgRPg57JfJgthiUD44nat7LjRRyhLpWxBbNowwDXGWzBz1dXbDz897Tr","DID":[["did:iamx:cardano:zggW2SuC7Phxth3SAjhtz7YuNfrcDhoRTz5WrSZ2xh38BTw","fXpi6R1vn5PzPFkBa48Ts5AjKfN7rv1XzSoG93PP46DbhrvQgrxeJVPJnE5zH7ZK","eSsBpx3J8qsFhwQMVLHNKfw3J3zPNuCQ9eAVtMiL1PJLdfWahW9cSWMPaLXyyyPB","H7oFC7HgjDcXkc2g7mnW2M2qp9Pz5iawLjWBrhfnQPBspTmfyttFJ4p8WJcVnd4x","hmtCm3ZMoGMWchMe3YojaazHKz6rVdk5eM6THH929oMb7kUT7USJgvTyavMCdjam","ksjKWn13omUAVu7LyPeo1cX1sWMf9x1t7EXr9yaBGrdV2aCaCpnaxUY71KEp6hhD","YiJoGk1LzDaos9RuDLvTEorQ2JSxSJHhCKSPLyye6w3rjRQ4gCTVH2N9ZuBqVPp6","UCjFjwiMBq5FS7MKnoYt7WoZwsUDGZPDwxAzATz8jSKd1SYcyYqeJtZYCRvcAZBe","EEgbAFrQwXJgDP3bvxWjvKy33DauoPabyC3WerA2HmH11Mtra4M458jpaJn5Rpva","BwiBuMQS4VvJa36qFVLbrZDMrUmgUUTuafCJTwpMPqcPm8G1T7MRoUpLwHo6vXA5","6hrmdcM1hKUEyHyNzjQEouW4J9erHvB5WYZP5yfkh3XeX1M5TxQMakPUzMGFwNsz","zpf6YrkYDS4NSRvAuL83HgUSibSqz1MNrJbougXEcWnY1y5g2gfa2u6yd6LQ2ZaT","PSAWfee3CesmzhBZEj2MEQYLdgNCr9nPWGdz6BDVZB2dfjg42EaDmEk3SbTqT9TS","ioQPrEYZgw1Z35BCT6U969ALBtUTeHpJrpcK7gSMjH4DcQ88EsBFFQuABroN5gJh","n4ZpwfJaKTMjUZZYVnmNY6emLVsmcMQmfPxv1qxhyYU7y7F2EGWGQtgiZE9Cndwe","5h1ULSVFhoyLV5cHkDz18RkETQ9DKqp1aLqNbDgwGtVGvJJjbRn4T4y2eJSNbGmp","G"]],"description":"Creator","storageLocation":{"uri":"ipfs://Qmb2uayBDYXquE8DAP99Jpukx7WoxvYNyjMkaTJ3Lwd1tG","type":"ipfs"}},{"signature":"Wtg7pRt7iZaJnsHNhgruy9pu9oGtLTVT3U8Yznf8JPqzr9FMT9NxweCedo5GcqhSo62eK452gtRUziY3kXuJN31A1Umqm8aq1LegysZNSenHmsY2zni3fdcJuURfYoyfxqRf6h21UKxek6kzbNYHSN6GdhaRS71MiDQaGYCiS2kcRdYYjTo8QkTYU6fY52Akx2fjrbRAjzmPCSHYzsyjCqAMKPgk7TpJxdt9RkPPV4WEq1WMngjC7nYJXYFFYhRbQe8aaC1pxsoYh9tjmTnkKKcZGuALNxou6hgE7Qgsn64zTdtvQqTy49z5itbvtfLscPwi8Y6pawH2fwX52kkFZQugaYuVLt8GyQBkSB16jy79vEb1CFtTa5w9CvTXzRENo6vj9v6ESuCcoebeVNskCboEgQoqHrEzbYsRTPw9ViVxb4pYwAwMzyExmsiohJxpc846ChEUmp7pnLMjp8TaUK7nScFVNagMoNHTUH7uuDyaqHPzyDGM9rz5w9uh3Kgg9QCwviwUpzcfz3xUJgPXmobFbxwQ17D1qGirXazXHBTLefhDVoyB2fz1oBbgnUoM5Q9WaWGCY9nTKKVKoTkLC8dap1XYECss6ghPQdnohY7mBFuLF23zSF1HzpBfYMDQXEFLdNRMefo7Drj1KE1YuopFpLasmTJEnvf8aHcNmX675iCUjZhbDjynfuEMEfky3hf2tu5AGEwWVBjMc6cAYYkqmKVdqcYiA84rgSXSJxho6bj1zeTgc2foX5nx5aktoR2AwDQfxcHQMRur5QTshFjSCcTB4j6tsMQSchvGz4Zhweu3MRp1AWApG8DMnxmQ4qAWmq1qs6MUhgDTJLMn8FRXzaWZ9Hiyr2pCj4nDMtt6a8G9RFpE92ZsUTXdZzhTZRvNK1HrsPHDD94NSG9a5kqGYEnrAcr7r3eV4Q","DID":[["did:iamx:cardano:zggW2SuC7Phxth3SAjhtz7YuNfrcDhoRTz5WrSZ2xh38BTw","fXpi6R1vn5PzPFkBZzQ3jvVAcFD5FK1nXLz7kLJn1cFEPFDUecSKaRNLsXBh5G6v","o8J2y5gBSVZG7pfwfvZVPvHLpSP2VQTmRDb34QfNGLsR8Pp2vRuhPDDs8REEtdZp","bcgy43XcHMqDtSdXoiFzPeAnSLWuaggTHJcDtAbD1Uc52hVqsHrTsjote441mCYk","SiUNdXDsVincFs2pTW7V3JRYnMrewb1GNtmnPLqSCzwkPkqDHuSYeprPZnHfC7Aj","svpWbueQHPT4gvUMz7XQ9xsUR5dPbQ4SMMf2teZ6DGiNFuc52Qo3SorohMdeLnp6","3uMuW4P8bnFqriwUH8JZvNNqPCwuDuJZ5YahSbqd6TNATLCutAGmJh5cnxr6RrWm","zNSmkBaPHcaVCHtLyAUiV1D7e1jzSsicYdk9P66ER4WezLQwyMSHvdT8Ww4iqC5L","PpGme2cPyAzEFTGp4hBK91pRbJfmY337GkGTGHLpvmg1TedvW3UxgUZap7uMNFiw","L5SYztY4K2xk7YmpXHZUwQGrRoDahGewEyPADJBK5hL3A863SccoUAeNBJhNhymB","83pEM2CgQVqqyHUZprHsZH2vZh4rpy8i1z84orfbjsfGfJSuGXHjxoJQPPCMYYpS","X8jjKNi7Z43vMqCYd4L97hkqg7wStYPeTHKii2gbVCzXHqxnMcrnrgUVk1fMhsJR","cUkcdrrGjpEDFMctE9ec4LcvJQbbxFULJCjxmYqeMEAxVVYoAwdp6cazQ3Bftety","4TSY2TRJnKDNqV9UR31mpTVFgY568GTLDYSMcvrXA13ACdztSL6dDuCZB67i9YuP","LVxnzFHwXdbN1nFV2oW3riAELL1WvACZoMq71J6GQEXKtWJKpgrREpgquQHNkyde","HcPiybAUMHFkZZ71rPXKJrra1CXAZS3E28zZiDuH6QHqZTPVjvXmAqbJCnJPuXma","c"]],"description":"IAMX","storageLocation":{"uri":"ipfs://QmZCDX15cMdUQa8apRM7UAu6HMxDe7r42LVcCUp95HfXue","type":"ipfs"}}]}"""

internal class IamXDidTest {

    @Test
    fun `isValidMatch verifies all accounts properly even if other accounts with same reference ID exist`() {
        listOf(
            Pair(GoogleAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME), "google"),
            Pair(TwitterAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME), "twitter"),
            Pair(DiscordAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME), "discord"),
            Pair(LinkedInAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME), "linkedin"),
            Pair(AppleAccount(DEFAULT_REFERENCE_ID), "apple"),
            Pair(TwitchAccount(DEFAULT_REFERENCE_ID), "twitch"),
            Pair(GitHubAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME), "github"),
        ).forEach {
            matchesAccountByTypeAndId(it.first, it.second, true, DEFAULT_REFERENCE_ID)
        }
    }

    @Test
    fun `isValidMatch rejects all accounts properly if service matches but ID does not`() {
        listOf(
            Pair(GoogleAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME), "google"),
            Pair(TwitterAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME), "twitter"),
            Pair(DiscordAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME), "discord"),
            Pair(LinkedInAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME), "linkedin"),
            Pair(AppleAccount(DEFAULT_REFERENCE_ID), "apple"),
            Pair(TwitchAccount(DEFAULT_REFERENCE_ID), "twitch"),
            Pair(GitHubAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME), "github"),
        ).forEach {
            matchesAccountByTypeAndId(it.first, it.second, false, "wrong")
        }
    }

    @Test
    fun `isValidMatch returns false for an unknown account`() {
        val did = IamXDid(
            IamXDidPayload(
                OffsetDateTime.now(),
                "7c0a7cff0859c6f7934200274e4996f616685bb01de0f4d605956a98",
                listOf(
                    UnknownAccount(mapOf("id" to DEFAULT_REFERENCE_ID))
                ),
                "1.0"
            )
        )
        assertFalse(did.isValidMatch("unknown", DEFAULT_REFERENCE_ID))
    }

    private fun matchesAccountByTypeAndId(
        referencedAccount: Account,
        typeString: String,
        isMatch: Boolean,
        referenceIdToMatch: String,
    ) {
        val otherAccount = if (typeString == "google") {
            GitHubAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME)
        } else {
            GoogleAccount(DEFAULT_REFERENCE_ID, DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME)
        }
        val did = IamXDid(
            IamXDidPayload(
                OffsetDateTime.now(),
                "7c0a7cff0859c6f7934200274e4996f616685bb01de0f4d605956a98",
                listOf(
                    otherAccount,
                    referencedAccount,
                ),
                "1.0"
            )
        )

        assertEquals(isMatch, did.isValidMatch(typeString, referenceIdToMatch))
    }

    @Test
    fun `parsing a IamX DID payload works`() {
        val om = ObjectMapper().registerModule(JavaTimeModule()).configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        val actualDid = om.readValue(PAYLOAD, IamXDid::class.java)
        val expectedDid = IamXDid(
            IamXDidPayload(
                Date(1708486681121).toInstant().atOffset(ZoneOffset.UTC),
                "d390811dc0e73d62d66eec7616a01d7951c286219e19e789c0d87d3e",
                listOf(
                    TwitterAccount("1111111111111111111", "somebodycodes"),
                    DiscordAccount("222222222222222222", "somebodycodes"),
                    GitHubAccount("3333333", "somebodycodes", "Somebody Codes"),
                    TwitchAccount("44444444"),
                    GoogleAccount("5555555", "somebodycodes", "Somebody Codes"),
                    LinkedInAccount("NAR-HARF", "somebodycodes", "Somebody Codes"),
                    AppleAccount("77777777"),
                    SteamAccount("Somebody Codes"),
                    UnknownAccount(mapOf("unknownId" to "88888888"))
                ),
                "1.0.0"
            )
        )
        assertEquals(expectedDid, actualDid)
    }

    @Test
    fun `parsing an IamX DID payload with an empty object for a policy ID works`() {
        val om = ObjectMapper().registerModule(JavaTimeModule()).configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        val actualDid = om.readValue(PAYLOAD_WITH_POLICYID_OBJECT, IamXDid::class.java)
        val expectedDid = IamXDid(
            IamXDidPayload(
                Date(1708486681121).toInstant().atOffset(ZoneOffset.UTC),
                null,
                listOf(
                    TwitterAccount("1111111111111111111", "somebodycodes"),
                    DiscordAccount("222222222222222222", "somebodycodes"),
                    GitHubAccount("3333333", "somebodycodes", "Somebody Codes"),
                    TwitchAccount("44444444"),
                    GoogleAccount("5555555", "somebodycodes", "Somebody Codes"),
                    LinkedInAccount("NAR-HARF", "somebodycodes", "Somebody Codes"),
                    AppleAccount("77777777"),
                    SteamAccount("Somebody Codes"),
                    UnknownAccount(mapOf("unknownId" to "88888888"))
                ),
                "1.0.0"
            )
        )
        assertEquals(expectedDid, actualDid)
    }

}