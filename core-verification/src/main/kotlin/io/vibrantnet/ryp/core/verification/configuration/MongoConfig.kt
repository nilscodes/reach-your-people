package io.vibrantnet.ryp.core.verification.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.TemporalAccessor
import java.util.*

@EnableReactiveMongoRepositories(
    basePackages = ["io.vibrantnet.ryp.core.verification.persistence"]
)
@EnableReactiveMongoAuditing(dateTimeProviderRef = "offsetDateTimeProvider")
@Configuration
@Profile("!test")
class MongoConfig {
    @Bean
    fun customConversions(): MongoCustomConversions {
        return MongoCustomConversions(listOf(
            ZonedDateTimeReadConverter(),
            ZonedDateTimeWriteConverter(),
            OffsetDateTimeReadConverter(),
            OffsetDateTimeWriteConverter(),
            OffsetDateTimeToLocalDateTimeConverter(),
            LocalDateTimeToOffsetDateTimeConverter(),
        ))
    }

    @Bean
    fun offsetDateTimeProvider() = CustomDateTimeProvider()
}

class CustomDateTimeProvider: DateTimeProvider {
    override fun getNow(): Optional<TemporalAccessor> {
        return Optional.of(OffsetDateTime.now())
    }
}

class ZonedDateTimeReadConverter : Converter<Date, ZonedDateTime> {
    override fun convert(date: Date): ZonedDateTime {
        return date.toInstant().atZone(ZoneOffset.UTC)
    }
}

class ZonedDateTimeWriteConverter : Converter<ZonedDateTime, Date> {
    override fun convert(zonedDateTime: ZonedDateTime): Date {
        return Date.from(zonedDateTime.toInstant())
    }
}

class OffsetDateTimeReadConverter : Converter<Date, OffsetDateTime> {
    override fun convert(date: Date): OffsetDateTime {
        return date.toInstant().atOffset(ZoneOffset.UTC)
    }
}

class OffsetDateTimeWriteConverter : Converter<OffsetDateTime, Date> {
    override fun convert(offsetDateTime: OffsetDateTime): Date {
        return Date.from(offsetDateTime.toInstant())
    }
}

class OffsetDateTimeToLocalDateTimeConverter : Converter<OffsetDateTime, LocalDateTime> {
    override fun convert(source: OffsetDateTime): LocalDateTime {
        return source.toLocalDateTime()
    }
}

class LocalDateTimeToOffsetDateTimeConverter : Converter<LocalDateTime, OffsetDateTime> {
    override fun convert(source: LocalDateTime): OffsetDateTime {
        return source.atOffset(ZoneOffset.UTC)
    }
}