package com.home.anotes.provider.payload.request

import com.home.anotes.provider.payload.model.NoteModel

data class BackupRequest(val notes: List<NoteModel>)
