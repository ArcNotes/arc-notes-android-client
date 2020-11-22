package com.home.anotes.provider.payload.model

import java.time.LocalDateTime

data class NoteModel(
    val title: String,
    val text: String,
    val pinned: Boolean,
    val reminderDate: LocalDateTime?,
    val creationDate: LocalDateTime?,
    val editDate: LocalDateTime?
)
