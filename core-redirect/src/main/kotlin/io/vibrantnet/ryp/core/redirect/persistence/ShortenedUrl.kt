package io.vibrantnet.ryp.core.redirect.persistence

import com.fasterxml.jackson.annotation.JsonFormat
import io.vibrantnet.ryp.core.redirect.model.ShortenedUrlDto
import io.vibrantnet.ryp.core.redirect.model.Status
import io.vibrantnet.ryp.core.redirect.model.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime
import java.util.*

@Document
data class ShortenedUrl(
    @Id
    val id: String,

    val url: String,

    @Indexed(unique = true)
    val shortcode: String,

    val type: Type,

    val status: Status,

    val projectId: Long?,

    val views: Long = 0,

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val createTime: OffsetDateTime = OffsetDateTime.now(),

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val modifiedTime: OffsetDateTime = OffsetDateTime.now(),
) {
    fun toDto() = ShortenedUrlDto(
        id = this.id,
        url = this.url,
        shortcode = this.shortcode,
        type = this.type,
        status = this.status,
        projectId = this.projectId,
        views = this.views,
        createTime = this.createTime,
    )
}



fun newEntity(id: UUID, shortcode: String, shortenedUrlDto: ShortenedUrlDto) = ShortenedUrl(
    id = id.toString(),
    url = shortenedUrlDto.url,
    shortcode = shortcode,
    type = shortenedUrlDto.type,
    status = shortenedUrlDto.status,
    projectId = shortenedUrlDto.projectId,
    views = shortenedUrlDto.views,
)