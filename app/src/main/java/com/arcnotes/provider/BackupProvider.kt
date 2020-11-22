package com.arcnotes.provider

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.arcnotes.provider.payload.request.AuthRequest
import com.arcnotes.provider.payload.request.BackupRequest
import com.arcnotes.provider.payload.response.BackupResponse
import com.arcnotes.provider.payload.response.RestoreResponse
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL


class BackupProvider {

    private val AUTH_ATTEMPTS_NUM = 2
    private val REQ_ATTEMPTS_NUM = 3
    private val REQ_DELAY: Long = 500

    private val AUTH_PATH = "/login"
    private val BACKUP_PATH = "/api/backup"
    private val RESTORE_PATH = "/api/restore"

    private var authToken: String? = null

    private val backupProps: BackupApiProps = BackupApiProps()
    private val httpClient: OkHttpClient = OkHttpClient()
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .findAndRegisterModules()

    /**
     * Executes a specified request.
     * Maps a response using provided response mapper.
     * @param <T> request object type.
     * @param <R> response object type.
     * @return mapped response object.
    </R></T> */
    private fun <T, R> execute(
        requestData: RequestData<T>,
        responseMapper: (Response) -> R?
    ): R? {
        Log.i("okHttpClient", "Executing request $requestData")

        // Build request url with params
        val url: String = backupProps.url + requestData.path
        val urlWithParams: String = (requestData.params ?: mapOf())
            .toList()
            .fold(URL(url).toHttpUrlOrNull()?.newBuilder()) { builder, pair ->
                builder?.addQueryParameter(pair.first, pair.second)
            }
            ?.build()
            ?.toString()
            .orEmpty()

        // Build request body
        val json: String? = requestData.payload?.let { objectMapper.writeValueAsString(it) }
        val requestBody: RequestBody? = json?.toRequestBody("application/json".toMediaTypeOrNull())

        // Build request
        val request: Request = Request.Builder()
            .url(urlWithParams)
            .headers(requestData.headers ?: Headers.headersOf())
            .method(requestData.method, requestBody)
            .build()

        // Execute request
        httpClient.newCall(request).execute().use {
            // Map response
            val responsePayload: R? = responseMapper(it)
            return responsePayload
        }
    }

    /**
     * Executes a specified request.
     * Provides a default response mapper for general requests.
     * Default mapper is able to:
     * - Automatically retries auth on auth failures.
     * - Automatically retries other requests on failure.
     * - Automatically maps a response JSON body into object of class respPayloadClass.
     * @param <T> request object type.
     * @param <R> response object type.
     * @return mapped response object.
    </R></T> */
    private fun <T, R> execute(
        requestData: RequestData<T>,
        respPayloadClass: Class<R>,
        attemptsLeft: Int
    ): R? {
        val defaultMapper: (Response) -> R? = { response ->
            // Validate response
            if (response.code == 403) {
                // Renew token
                Log.e("okHttpClient", "Auth failure")
                this.authToken = updateAuthToken(AUTH_ATTEMPTS_NUM)
                Log.i("okHttpClient","Auth token was successfully updated")
                val updRequestData = requestData.copy(headers = genHeaders())

                execute(updRequestData, respPayloadClass, attemptsLeft)
            } else if (response.code != 200) {
                // Retry if it available
                val attemptsLeft = attemptsLeft - 1
                if (attemptsLeft > 0) {
                    Log.e(
                        "okHttpClient",
                        "Request has been forbidden, attempts left=$attemptsLeft, request=$requestData",
                    )
                    Thread.sleep(REQ_DELAY)
                    execute(requestData, respPayloadClass, attemptsLeft)
                }
                throw RuntimeException(response.code.toString())
            } else {
                // Parse response payload
                val responseJson: String? = response.body?.string()
                val responsePayload: R? = responseJson?.let {
                    objectMapper.readValue(it, respPayloadClass)
                }

                responsePayload
            }
        }
        return execute(requestData, defaultMapper)
    }

    private fun updateAuthToken(attemptsLeft: Int): String {
        val authPayload = AuthRequest(backupProps.adminUsername, backupProps.adminPassword)
        val authReq: RequestData<AuthRequest> = RequestData(
            AUTH_PATH,
            "POST",
            null,
            null,
            authPayload
        )
        val authMapper: (Response) -> String? = { response ->
            // Validate response
            if (response.code != 200) {
                // Retry if it available
                val attemptsLeft = attemptsLeft - 1
                if (attemptsLeft > 0) {
                    Log.e(
                        "okHttpClient",
                        "Login request.code != 200, attempts left=$attemptsLeft",
                    )
                    Thread.sleep(REQ_DELAY)
                    updateAuthToken(attemptsLeft)
                }
                throw RuntimeException("Cannot login")
            }

            // Parse response payload
            val authHeader: String? = response.header(backupProps.authHeader)
            val token: String? = authHeader?.trim()
                ?.replace(backupProps.authTokenPrefix, "")

            token
        }

        val authToken: String = execute(authReq, authMapper)
            ?: throw RuntimeException("Cannot extract JWT token")

        return authToken
    }

    private fun genHeaders(): Headers {
        return Headers.headersOf(
            "Content-Type", "application/json",
            backupProps.authHeader, backupProps.authTokenPrefix + this.authToken.orEmpty()
        )
    }

    fun restore(): RestoreResponse? {
        val requestData = RequestData<Any>(
            RESTORE_PATH,
            "GET",
            genHeaders(),
            null,
            null
        )
        val response = execute(requestData, RestoreResponse::class.java, REQ_ATTEMPTS_NUM)
        return response
    }

    fun backup(toBackup: BackupRequest): BackupResponse? {
        val requestData = RequestData(
            BACKUP_PATH,
            "POST",
            genHeaders(),
            null,
            toBackup
        )
        val response = execute(requestData, BackupResponse::class.java, REQ_ATTEMPTS_NUM)
        return response
    }

    private data class RequestData<M>(
        val path: String,
        val method: String,
        val headers: Headers? = null,
        val params: Map<String, String>? = null,
        val payload: M? = null
    )
}
