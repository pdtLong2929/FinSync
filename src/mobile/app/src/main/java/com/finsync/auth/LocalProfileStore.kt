package com.finsync.auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Private app storage, scoped to the server's user UUID. Never stores credentials. */
class LocalProfileStore(context: Context) {
    private val app = context.applicationContext
    private fun key(id: String) = "profile_${UUID.fromString(id)}"
    private fun avatarDir() = File(app.filesDir, "profile-avatars").also { check(it.exists() || it.mkdirs()) }

    suspend fun load(user: AuthUser): LocalProfile = withContext(Dispatchers.IO) {
        val preferences = app.getSharedPreferences(key(user.id), Context.MODE_PRIVATE)
        LocalProfile(user.id, preferences.getString("name", null) ?: user.displayName,
            preferences.getString("phone", "").orEmpty(), preferences.getString("birthday", "").orEmpty(),
            preferences.getString("bio", "").orEmpty(), preferences.getString("avatar", null)?.let { name ->
                File(avatarDir(), File(name).name).takeIf { it.isFile }?.absolutePath
            })
    }

    suspend fun save(profile: LocalProfile): LocalProfile = withContext(Dispatchers.IO) {
        require(!validateProfile(profile).any)
        val clean = profile.copy(displayName = profile.displayName.trim(), phone = profile.phone.trim(), bio = profile.bio.trim())
        val saved = app.getSharedPreferences(key(clean.userId), Context.MODE_PRIVATE).edit()
            .putString("name", clean.displayName).putString("phone", clean.phone).putString("birthday", clean.birthday)
            .putString("bio", clean.bio).putString("avatar", clean.avatarPath?.let { File(it).name }).commit()
        check(saved) { "Profile storage unavailable" }
        clean
    }

    /** Decode a bounded thumbnail and re-encode pixels, discarding original metadata. */
    suspend fun importAvatar(uri: Uri): String = withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        app.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, options) }
        require(options.outWidth > 0 && options.outHeight > 0 && options.outWidth <= 30_000 && options.outHeight <= 30_000)
        var sample = 1
        while (maxOf(options.outWidth, options.outHeight) / sample > 1024) sample *= 2
        val decoded = app.contentResolver.openInputStream(uri).use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: error("Cannot read photo")
        try {
            val side = minOf(decoded.width, decoded.height)
            val square = Bitmap.createBitmap(decoded, (decoded.width - side) / 2, (decoded.height - side) / 2, side, side)
            try {
                val photo = File(avatarDir(), "${UUID.randomUUID()}.jpg")
                try { photo.outputStream().use { check(square.compress(Bitmap.CompressFormat.JPEG, 88, it)) } }
                catch (error: Exception) { photo.delete(); throw error }
                photo.absolutePath
            } finally { if (square !== decoded) square.recycle() }
        } finally { decoded.recycle() }
    }

    suspend fun discardAvatar(path: String?) = withContext(Dispatchers.IO) {
        if (path != null) {
            val file = File(path).canonicalFile
            if (file.parentFile == avatarDir().canonicalFile) file.delete()
        }
        Unit
    }
}
