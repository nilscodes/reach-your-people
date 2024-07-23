package io.vibrantnet.ryp.core.verification.persistence

import com.fasterxml.jackson.annotation.JsonFormat
import io.ryp.cardano.model.StakepoolVerificationDto
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime

@Document
data class StakepoolVerificationDocument(
    @Id
    val verificationNonce: String,

    val verificationData: StakepoolVerificationDto,

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val createdDate: OffsetDateTime = OffsetDateTime.now(),
)