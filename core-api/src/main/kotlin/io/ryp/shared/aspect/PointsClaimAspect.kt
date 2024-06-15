package io.ryp.shared.aspect

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ryp.shared.model.points.PointsClaimDto
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.KotlinReflectionParameterNameDiscoverer
import org.springframework.expression.spel.standard.SpelExpressionParser
import reactor.core.publisher.Mono
import java.lang.reflect.Method

val logger = KotlinLogging.logger {}

@Aspect
class PointsClaimAspect(
    private val rabbitTemplate: RabbitTemplate,
    private val queueName: String,
    private val rypTokenId: Int,
    private val pointsMap: Map<String, Long>,
) {

    private val parser = SpelExpressionParser()

    @Pointcut("@annotation(io.ryp.shared.aspect.PointsClaim) || @annotation(io.ryp.shared.aspect.PointsClaims)")
    fun pointsClaimMethods() {
        // Just for AOP purposes
    }

    @AfterReturning(pointcut = "pointsClaimMethods()", returning = "result")
    fun afterReturningAdvice(
        joinPoint: JoinPoint,
        result: Any?
    ) {
        val method = (joinPoint.signature as MethodSignature).method
        val context =
            MethodBasedEvaluationContext(joinPoint, method, joinPoint.args, KotlinReflectionParameterNameDiscoverer())
        context.setVariable("points", pointsMap)
        context.setVariable("rypTokenId", rypTokenId)

        val pointsClaims = method.getAnnotationsByType(PointsClaim::class.java)

        if (result is Mono<*>) {
            result.subscribe {
                context.setVariable("result", it)
                processPointsClaim(pointsClaims, context, method)
            }
        } else {
            context.setVariable("result", result)
            processPointsClaim(pointsClaims, context, method)
        }

    }

    private fun processPointsClaim(
        pointsClaims: Array<PointsClaim>,
        context: MethodBasedEvaluationContext,
        method: Method
    ) {
        pointsClaims.forEach { pointsClaimSettings ->
            try {
                val points = parser.parseExpression(pointsClaimSettings.points).getValue(context, Long::class.java)!!
                val category =
                    parser.parseExpression(pointsClaimSettings.category).getValue(context, String::class.java)!!
                val claimId =
                    parser.parseExpression(pointsClaimSettings.claimId).getValue(context, String::class.java)!!
                val accountIdString =
                    parser.parseExpression(pointsClaimSettings.accountId).getValue(context, String::class.java)
                val tokenId = parser.parseExpression(pointsClaimSettings.tokenId).getValue(context, Int::class.java)!!
                val claimed =
                    parser.parseExpression(pointsClaimSettings.claimed).getValue(context, Boolean::class.java)!!
                val projectId =
                    if (pointsClaimSettings.projectId.isNotBlank()) parser.parseExpression(pointsClaimSettings.projectId)
                        .getValue(context, Long::class.java) else null

                if (accountIdString != null) {
                    val pointsClaim = PointsClaimDto(
                        points = points,
                        category = category,
                        claimId = claimId,
                        accountId = accountIdString.toLong(),
                        tokenId = tokenId,
                        claimed = claimed,
                        projectId = projectId,
                    )

                    logger.debug { "Points claim aspect triggered after method ${method.name}, sending payload $pointsClaim" }
                    rabbitTemplate.convertAndSend(queueName, pointsClaim)
                } else {
                    logger.debug { "Points claim aspect for method ${method.name} has no account ID, skipping. Settings were: $pointsClaimSettings" }
                }
            } catch (e: Exception) {
                logger.error(e) {
                    "Error processing points claim aspect for method: ${method.name}"
                }
            }
        }
    }
}
