package io.chthonic.mechanicuslovecraft.data.common.json

import com.squareup.moshi.*
import io.chthonic.mechanicuslovecraft.common.valueobjects.Role

internal class RoleJsonAdapter : JsonAdapter<Role>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Role? =
        if (reader.peek() != JsonReader.Token.NULL) {
            Role.validate(reader.nextString())
        } else {
            reader.nextNull()
        }

    @ToJson
    override fun toJson(writer: JsonWriter, role: Role?) {
        writer.value(role?.value)
    }
}