package com.home.arcnotes.provider.payload.response

import java.time.LocalDateTime

data class BackupResponse(
    val userId: Long,
    val snapshotMd5: String,
    val creationDate: LocalDateTime
)