package com.home.arcnotes.provider.payload.response

import com.home.arcnotes.provider.payload.model.NoteModel

data class RestoreResponse(val notes: List<NoteModel>)
