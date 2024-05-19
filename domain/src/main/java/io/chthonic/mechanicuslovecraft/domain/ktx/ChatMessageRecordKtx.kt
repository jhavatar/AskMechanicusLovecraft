package io.chthonic.mechanicuslovecraft.domain.ktx

import io.chthonic.mechanicuslovecraft.domain.dataapi.models.ChatMessageRecord

fun ChatMessageRecord.toPresentationModel(): io.chthonic.mechanicuslovecraft.domain.presentationapi.models.ChatMessage =
    io.chthonic.mechanicuslovecraft.domain.presentationapi.models.ChatMessage(
        index = this.index,
        created = this.created,
        role = this.value.role,
        content = this.value.content,
        isDone = this.isDone,
        isError = this.isError,
    )