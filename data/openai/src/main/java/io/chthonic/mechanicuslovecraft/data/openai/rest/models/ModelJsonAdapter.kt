package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import com.squareup.moshi.*

internal class ModelJsonAdapter : JsonAdapter<Model>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Model? =
        if (reader.peek() != JsonReader.Token.NULL) {
            Model(reader.nextString())
        } else {
            reader.nextNull()
        }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Model?) {
        writer.value(value?.value)
    }
}