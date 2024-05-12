package io.chthonic.mechanicuslovecraft.data.openai.rest.models

import com.squareup.moshi.*
import io.chthonic.mechanicuslovecraft.domain.dataapi.openai.models.GptModel

internal class GptModelJsonAdapter : JsonAdapter<GptModel>() {

    @FromJson
    override fun fromJson(reader: JsonReader): GptModel? =
        if (reader.peek() != JsonReader.Token.NULL) {
            GptModel(reader.nextString())
        } else {
            reader.nextNull()
        }

    @ToJson
    override fun toJson(writer: JsonWriter, model: GptModel?) {
        writer.value(model?.value)
    }
}