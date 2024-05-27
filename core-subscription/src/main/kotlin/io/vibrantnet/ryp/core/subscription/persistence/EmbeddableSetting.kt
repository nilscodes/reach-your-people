package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.SettingDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class EmbeddableSetting(
    @Column(name = "setting_name")
    var name: String,

    @Column(name = "setting_value")
    var value: String
) {
    fun toDto() = SettingDto(name, value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmbeddableSetting) return false

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString(): String {
        return "EmbeddableSetting(name=$name, value=$value)"
    }
}