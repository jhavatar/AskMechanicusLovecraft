package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import com.squareup.moshi.*

internal class RoleJsonAdapter : JsonAdapter<Role>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Role? =
        if (reader.peek() != JsonReader.Token.NULL) {
            Role(reader.nextString())
        } else {
            reader.nextNull()
        }

    @ToJson
    override fun toJson(writer: JsonWriter, role: Role?) {
        writer.value(role?.value)
    }
}