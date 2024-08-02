package io.vibrantnet.ryp.core.billing.service

import io.ryp.core.billing.model.BillDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BillingApiService {

    /**
     * POST /billing/accounts/{accountId} : Create new bill
     * Create a bill for an account
     *
     * @param accountId The numeric ID of an account (required)
     * @param bill  (optional)
     * @return OK (status code 200)
     * @see BillingApi#createBill
     */
    fun createBill(accountId: Long, bill: BillDto): Mono<BillDto>

    /**
     * GET /billing/accounts/{accountId} : Get all bills for this account
     * Get a list of all open and paid bills for an account
     *
     * @param accountId The numeric ID of an account (required)
     * @return OK (status code 200)
     * @see BillingApi#getBillsForAccount
     */
    fun getBillsForAccount(accountId: Long): Flux<BillDto>
}
