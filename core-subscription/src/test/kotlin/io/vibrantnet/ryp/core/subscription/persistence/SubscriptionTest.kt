package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.DefaultSubscriptionStatus
import io.vibrantnet.ryp.core.subscription.model.ProjectSubscriptionDto
import io.vibrantnet.ryp.core.subscription.model.SubscriptionStatus
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SubscriptionTest {

        @Test
        fun testToDto() {
            val subscription = Subscription(1, SubscriptionStatus.SUBSCRIBED)
            val dto = subscription.toDto()
            assertEquals(ProjectSubscriptionDto(1, DefaultSubscriptionStatus.UNSUBSCRIBED, SubscriptionStatus.SUBSCRIBED), dto)
        }

        @Test
        fun testEqualsAndHashCode() {
            EqualsVerifier.forClass(Subscription::class.java)
                .withIgnoredFields("status")
                .verify()
        }

}