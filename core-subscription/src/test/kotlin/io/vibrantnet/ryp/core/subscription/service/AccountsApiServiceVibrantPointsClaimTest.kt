package io.vibrantnet.ryp.core.subscription.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import io.ryp.shared.aspect.PointsClaimAspect
import io.ryp.shared.model.points.PointsClaimDto
import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.persistence.Account
import io.vibrantnet.ryp.core.subscription.persistence.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@EnableAutoConfiguration(exclude = [RabbitAutoConfiguration::class])
@ActiveProfiles("test")
class AccountsApiServiceVibrantPointsClaimTest {

    @MockkBean
    private lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    private lateinit var accountsApiService: AccountsApiService

    @MockkBean
    private lateinit var accountRepository: AccountRepository

    @TestConfiguration
    class TestConfig {

        @Bean
        @Primary
        fun pointsClaimAspect(rabbitTemplate: RabbitTemplate) = PointsClaimAspect(
            rabbitTemplate, "test", 12, mapOf("referral" to 1, "signup" to 1000)
        )
    }

    @BeforeEach
    fun setUp() {
        // Return the first argument of the function but with an account ID
        every { accountRepository.save(any()) } answers { firstArg<Account>().apply { id = 13 } }
        every { rabbitTemplate.convertAndSend(any(String::class), any(Any::class)) } just runs
    }

    @Test
    fun `correct points claim requested when signing up without referrer`() {
        accountsApiService.createAccount(AccountDto("joe"), null)
            .subscribe()
        verify(exactly = 1) {
            rabbitTemplate.convertAndSend("test", match<PointsClaimDto> {
                it.accountId == 13L && it.points == 1000L && it.category == "signup" && it.claimId == "signup-13" && it.claimed
            })
        }
    }

    @Test
    fun `correct points claim requested when signing up with referrer`() {
        accountsApiService.createAccount(AccountDto("joe"), 69)
            .subscribe()
        verify(exactly = 1) {
            rabbitTemplate.convertAndSend("test", match<PointsClaimDto> {
                it.accountId == 13L && it.points == 1000L && it.category == "signup" && it.claimId == "signup-13" && it.claimed
            })
        }
        verify(exactly = 1) {
            rabbitTemplate.convertAndSend("test", match<PointsClaimDto> {
                it.accountId == 69L && it.points == 1L && it.category == "referral" && it.claimId == "referral-13" && it.claimed
            })
        }
    }

}