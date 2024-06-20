package io.vibrantnet.ryp.core.subscription.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

class ProjectNotificationSettingTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ProjectNotificationSetting::class.java)
            .verify()
    }
}