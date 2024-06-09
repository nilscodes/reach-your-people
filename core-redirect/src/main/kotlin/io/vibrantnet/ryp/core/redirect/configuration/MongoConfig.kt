package io.vibrantnet.ryp.core.redirect.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

@Configuration
class MongoConfig {
    @Bean
    fun customConversions(): MongoCustomConversions {
        return MongoCustomConversions(listOf(
            ZonedDateTimeReadConverter(),
            ZonedDateTimeWriteConverter()
        ))
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