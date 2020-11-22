package com.arcnotes.provider.payload.request

import com.arcnotes.provider.payload.model.NoteModel

data class BackupRequest(val notes: List<NoteModel>)
