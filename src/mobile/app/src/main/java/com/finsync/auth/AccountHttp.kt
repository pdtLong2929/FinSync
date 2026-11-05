package com.finsync.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URI
import java.net.URL

internal class AccountReply(val status: Int, val body: JSONObject) {
    override fun toString() = "AccountReply(status=$status, body=<redacted>)"
    fun checked(): JSONObject {
        if (status in 200..299) return body
        val reason = when (body.optString("code")) {
            "INVALID_CREDENTIALS" -> AuthFailure.InvalidCredentials
            "UNAUTHORIZED", "INVALID_REFRESH_TOKEN" -> AuthFailure.SessionExpired
            "ACCOUNT_LOCKED" -> AuthFailure.AccountLocked
            "EMAIL_ALREADY_EXISTS" -> AuthFailure.EmailExists
            "PROFILE_VERSION_CONFLICT" -> AuthFailure.ProfileConflict
            "VALIDATION_ERROR" -> AuthFailure.InvalidInput
            "AUTH_SERVICE_UNAVAILABLE" -> AuthFailure.Unavailable
            else -> when (status) { 400, 422 -> AuthFailure.InvalidInput; 401 -> AuthFailure.SessionExpired
                403 -> AuthFailure.AccountLocked; 429 -> AuthFailure.RateLimited; else -> AuthFailure.Unavailable }
        }
        val fields = body.optJSONObject("fieldErrors")
        fun field(key: String) = fields?.optString(key)?.takeIf { it.isNotBlank() }?.take(180)
        throw AccountApiException(reason, AuthFieldErrors(email = field("email"), password = field("password"), displayName = field("displayName")))
    }
}

internal class AccountHttp(baseUrl: String, allowLocalHttp: Boolean) {
    private val base = baseUrl.trimEnd('/').also {
        val uri = URI(it)
        require(uri.host != null && uri.userInfo == null && uri.query == null && uri.fragment == null)
        require(uri.scheme == "https" || (allowLocalHttp && uri.scheme == "http" &&
            uri.host in setOf("10.0.2.2", "127.0.0.1", "localhost")))
    }
    suspend fun call(method: String, path: String, payload: JSONObject? = null, token: String? = null): AccountReply = withContext(Dispatchers.IO) {
        val connection = URL("$base/api/v1/$path").openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method; connection.instanceFollowRedirects = false
            connection.connectTimeout = 10_000; connection.readTimeout = 10_000
            connection.setRequestProperty("Accept", "application/json")
            if (token != null) connection.setRequestProperty("Authorization", "Bearer $token")
            if (payload != null) {
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doOutput = true
                connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
            }
            val status = connection.responseCode
            currentCoroutineContext().ensureActive()
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            // Avoid readNBytes: the client supports API26.
            val buffer = ByteArray(65_537); var length = 0
            stream?.use { while (length < buffer.size) { val count = it.read(buffer, length, buffer.size - length); if (count < 0) break; length += count } }
            require(length <= 65_536)
            currentCoroutineContext().ensureActive()
            AccountReply(status, if (length == 0) JSONObject() else JSONObject(String(buffer, 0, length, Charsets.UTF_8)))
        } catch (cancelled: kotlinx.coroutines.CancellationException) { throw cancelled }
        catch (failed: AccountApiException) { throw failed }
        catch (_: SocketTimeoutException) { throw AccountApiException(AuthFailure.Unavailable) }
        catch (_: IOException) { throw AccountApiException(AuthFailure.Offline) }
        catch (_: Exception) { throw AccountApiException(AuthFailure.Unavailable) }
        finally { connection.disconnect() }
    }
}
