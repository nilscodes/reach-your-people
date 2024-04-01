package io.vibrantnet.ryp.core.publishing.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.ZonedDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
sealed class ActivityStreamsObject

@JsonTypeName("Person")
data class Person(
    val name: String,
    val url: String,
) : ActivityStreamsObject()

@JsonTypeName("Organization")
data class Organization(
    val name: String,
    val url: String,
) : ActivityStreamsObject()

@JsonTypeName("Note")
data class Note(
    val content: String,
) : ActivityStreamsObject()

@JsonTypeName("Event")
data class Event(
    val name: String,
    val content: String,
    val url: String,
    @JsonProperty("startTime") val startTime: ZonedDateTime,
    @JsonProperty("endTime") val endTime: ZonedDateTime,
) : ActivityStreamsObject()

@JsonTypeName("Announce")
data class AnnounceActivity(
    val actor: ActivityStreamsObject,
    val `object`: ActivityStreamsObject,
) : ActivityStreamsObject()

data class ActivityStream(
    @JsonProperty("@context")
    val context: String = "https://www.w3.org/ns/activitystreams",
    val id: String,
    val type: String = "Announce",
    val actor: ActivityStreamsObject,
    val `object`: ActivityStreamsObject,
)