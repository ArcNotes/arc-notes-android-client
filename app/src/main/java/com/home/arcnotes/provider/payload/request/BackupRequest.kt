package com.home.arcnotes.provider.payload.request

import com.home.arcnotes.provider.payload.model.NoteModel

data class BackupRequest(val notes: List<NoteModel>)
