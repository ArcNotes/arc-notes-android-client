package com.arcnotes.provider.payload.response

import com.arcnotes.provider.payload.model.NoteModel

data class RestoreResponse(val notes: List<NoteModel>)
