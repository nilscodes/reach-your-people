package io.vibrantnet.ryp.core.subscription.persistence

import org.springframework.data.repository.CrudRepository

interface AccountRepository: CrudRepository<Account, Long>