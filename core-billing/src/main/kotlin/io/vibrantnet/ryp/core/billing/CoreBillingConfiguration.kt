package io.vibrantnet.ryp.core.billing

import io.ryp.shared.SecurityConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CoreBillingConfiguration(
    val verifyServiceUrl: String,
    val payment: PaymentConfiguration = PaymentConfiguration(),
    val security: SecurityConfiguration = SecurityConfiguration(),
)

data class PaymentConfiguration(
    val receiveAddress: String? = null, // If no receive address is present, on-chain payments are disabled/not checked
    val transactionOrderTimeToleranceSeconds: Long = 60, // 1 minute
)