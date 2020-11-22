package com.home.arcnotes.provider

data class BackupApiProps(
    val url: String = "http://192.168.0.102:8080",
    val authHeader: String = "Authorization",
    val authTokenPrefix: String = "Bearer ",
    val adminUsername: String = "admin",
    val adminPassword: String = "xyz3000",
)