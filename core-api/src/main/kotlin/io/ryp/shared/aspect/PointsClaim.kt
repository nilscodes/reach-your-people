package io.ryp.shared.aspect

import java.lang.annotation.Repeatable

/**
 * Annotation to have a cross-cutting concern to allow for point claims to be issued without polluting the business logic.
 * Can be used multiple times on the same method to allow issuing multiple claims,
 * like during signup for the new user and a potential referrer.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable(PointsClaims::class)
annotation class PointsClaim(
    /**
     * SpEL expression to evaluate the points to be claimed.
     * Can use the #points variable to access the points map available to the aspect.
     * <br />
     * Example: "#points['referral']"
     */
    val points: String,

    /**
     * The category of the claim as an arbitrary string
     */
    val category: String,

    /**
     * The claim ID must be a unique identifier across ALL claims, across the entire userbase.
     * Depending on if two people should be able to claim the same thing, the developer should design the respective ID accordingly.
     * Include the account ID in the ID if the claim is account-specific, for example.
     */
    val claimId: String,

    /**
     * The account ID of the user who is claiming the points.
     */
    val accountId: String,

    /**
     * The ID of the token to be used for the points claim. Defaults to "#rypTokenId" which will be the default token for RYP, provided through the aspect configuration class.
     */
    val tokenId: String = "#rypTokenId",

    /**
     * An optional project ID to associate the points with if they should be trackable by project owners or attributed to a project in another way.
     */
    val projectId: String = "",

    /**
     * If the points are immediately claimed by the user (default), or if this just enables the points to be claimed by a specific activity by a user later.
     */
    val claimed: String = "true",
)

/**
 * Annotation to support tracking multiple points claims on a single method.
 * You do not need to use this annotation explicitly, as the @Repeatable annotation
 * on PointsClaim will allow you to use multiple PointsClaim annotations on the same method.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PointsClaims(
    val value: Array<PointsClaim>
)