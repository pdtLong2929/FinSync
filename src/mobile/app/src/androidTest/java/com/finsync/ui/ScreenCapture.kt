package com.finsync.ui
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.platform.app.InstrumentationRegistry

/** Test-only app pixels. Avoid shell screencap, which crashes this emulator. */
fun captureScreen(name: String, compose: ComposeTestRule, nativeWindow: Boolean = false) {
    require(name.matches(Regex("[a-z0-9-]+")))
    compose.mainClock.advanceTimeBy(500) // Finish tap ripple before material review.
    compose.waitForIdle()
    // Native dialog/window animations use wall time, outside Compose's test clock.
    if (nativeWindow) android.os.SystemClock.sleep(400)
    var captured: Bitmap? = null
    compose.waitUntil(timeoutMillis = 5_000) {
        val frame = if (nativeWindow) requireNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot())
            else runCatching { compose.onAllNodes(isRoot()).onLast().captureToImage().asAndroidBitmap() }
                .getOrElse { requireNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()) }
        val colors = mutableSetOf<Int>()
        for (y in 0 until frame.height step maxOf(1, frame.height / 30))
            for (x in 0 until frame.width step maxOf(1, frame.width / 20)) colors.add(frame.getPixel(x, y))
        (colors.size > 8).also { if (it) captured = frame }
    }
    val bitmap = requireNotNull(captured)
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val filename = "finsync-full-$name-${System.currentTimeMillis()}.png"
    if (Build.VERSION.SDK_INT >= 29) {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/FinSyncReview")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val uri = requireNotNull(resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values))
        requireNotNull(resolver.openOutputStream(uri)).use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        values.clear()
        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    } else {
        java.io.File(context.getExternalFilesDir(null), filename).outputStream().use {
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
    }
}
