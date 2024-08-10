package io.vibrantnet.ryp.core.verification.service

import com.muquit.libsodiumjna.SodiumLibrary
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException
import io.vibrantnet.ryp.core.verification.CoreVerificationConfiguration
import org.springframework.stereotype.Service

@Service
class LibSodiumService(
    config: CoreVerificationConfiguration,
) {
    init {
        SodiumLibrary.setLibraryPath(config.libsodiumPath)
    }

    @Throws(SodiumLibraryException::class)
    fun cryptoBlake2bHash(`in`: ByteArray?, key: ByteArray?): ByteArray = SodiumLibrary.cryptoBlake2bHash(`in`, key)

    @Throws(SodiumLibraryException::class)
    fun cryptoVrfProofToHash_ietfdraft03(proof: ByteArray?): ByteArray = SodiumLibrary.cryptoVrfProofToHash_ietfdraft03(proof)

    @Throws(SodiumLibraryException::class)
    fun cryptoVrfVerify_ietfdraft03(vrfVkey: ByteArray?, proof: ByteArray?, m: ByteArray?): ByteArray = SodiumLibrary.cryptoVrfVerify_ietfdraft03(vrfVkey, proof, m)

}