package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import java.io.IOException

@JsonSerialize(using = SettingsDtoSerializer::class)
data class SettingsDto(
    @field:NotNull
    val settings: Set<SettingDto>,
)

data class SettingDto @JsonCreator constructor(
    @field:NotNull
    @field:Size(min = 1, max = 64)
    @field:Pattern(regexp = "^[_A-Z0-9]+$")
    val name: String,

    @field:NotNull
    @field:Size(min = 0, max = 4096)
    val value: String,
)

class SettingsDtoSerializer : StdSerializer<SettingsDto>(SettingsDto::class.java) {

    @Throws(IOException::class)
    override fun serialize(settings: SettingsDto, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeStartObject()
        for (setting in settings.settings) {
            jsonGenerator.writeStringField(setting.name, setting.value)
        }
        jsonGenerator.writeEndObject()
    }
}