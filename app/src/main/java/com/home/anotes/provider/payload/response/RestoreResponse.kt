package com.home.anotes.provider.payload.response

import com.home.anotes.provider.payload.model.NoteModel

data class RestoreResponse(val notes: List<NoteModel>)
