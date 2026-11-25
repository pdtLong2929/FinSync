package com.finsync.bank

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.finsync.MainActivity
import com.finsync.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** System-bound opt-in listener. No SMS, accessibility, bank credentials or network upload. */
class BankNotificationListener : NotificationListenerService() {
    companion object {
        private val connection = MutableStateFlow(false)
        val connected = connection.asStateFlow()
    }
    override fun onListenerConnected() { super.onListenerConnected(); connection.value = true }
    override fun onListenerDisconnected() { connection.value = false; super.onListenerDisconnected() }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        val settings = BankCaptureSettings(this)
        val consent = settings.read()
        val now = System.currentTimeMillis()
        if (!consent.enabled || sbn.packageName !in consent.packages || sbn.packageName == packageName ||
            sbn.postTime < consent.enabledAt || now - sbn.postTime !in -10_000..300_000) return
        val notification = sbn.notification
        if (notification.flags and (Notification.FLAG_GROUP_SUMMARY or Notification.FLAG_ONGOING_EVENT) != 0) return
        val title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val body = (notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
            ?: notification.extras.getCharSequence(Notification.EXTRA_TEXT))?.toString().orEmpty()
        val candidate = BankNotificationParser.parse(sbn.packageName, sbn.key, sbn.postTime,
            notification.`when`.takeIf { it > 0 } ?: sbn.postTime, "$title $body") ?: return
        scope.launch {
            try {
                var inserted = false
                FileBankStore(applicationContext, consent.workspace).edit { data ->
                    val current = settings.read()
                    if (!current.enabled || current.workspace != consent.workspace || current.enabledAt > candidate.observedAt || candidate.sourcePackage !in current.packages ||
                        data.candidates.any { it.id == candidate.id }) data
                    else {
                        require(data.candidates.size < 10_000)
                        inserted = true
                        val duplicate = candidate.amount != null && data.candidates.any {
                            it.sourcePackage == candidate.sourcePackage && it.amount == candidate.amount && it.direction == candidate.direction &&
                                kotlin.math.abs(it.observedAt - candidate.observedAt) < 120_000
                        }
                        data.copy(candidates = data.candidates + candidate.copy(possibleDuplicate = duplicate))
                    }
                }
                val current = settings.read()
                if (inserted && current.enabled && current.workspace == consent.workspace) BankReminder.show(applicationContext, candidate.id)
            } catch (_: Exception) {
                try { settings.reportCaptureError(consent.workspace) } catch (_: Exception) { /* No raw payload or diagnostics are logged. */ }
            }
        }
    }
    override fun onDestroy() { connection.value = false; scope.cancel(); super.onDestroy() }
}

object BankReminder {
    const val ACTION = "com.finsync.REVIEW_BANK_ALERTS"
    const val CHANNEL = "bank-review-popups"
    fun ensureChannel(context: android.content.Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL) == null) {
            val old = manager.getNotificationChannel("bank-review")
            val importance = if (old?.importance == NotificationManager.IMPORTANCE_NONE) NotificationManager.IMPORTANCE_NONE else NotificationManager.IMPORTANCE_HIGH
            manager.createNotificationChannel(NotificationChannel(CHANNEL, "Bank transaction prompts", importance)
                .apply { description = "Pop-up prompts to choose a wallet and purpose"; lockscreenVisibility = Notification.VISIBILITY_PRIVATE })
        }
    }
    fun show(context: android.content.Context, id: String) {
        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val manager = context.getSystemService(NotificationManager::class.java)
        if (!manager.areNotificationsEnabled()) return
        ensureChannel(context)
        val intent = Intent(context, MainActivity::class.java).setAction(ACTION)
            .setData(Uri.parse("finsync://bank-alert/$id")).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = Notification.Builder(context, CHANNEL).setSmallIcon(R.drawable.ic_wallet)
            .setContentTitle("New bank transaction")
            .setContentText("Choose a wallet and purpose to save it.")
            .setCategory(Notification.CATEGORY_REMINDER).setOnlyAlertOnce(true)
            .setVisibility(Notification.VISIBILITY_PRIVATE).setContentIntent(pending).setAutoCancel(true)
            .setPublicVersion(Notification.Builder(context, CHANNEL).setSmallIcon(R.drawable.ic_wallet).setContentTitle("FinSync").setContentText("A bank alert is ready for review.").build())
            .build()
        manager.notify("bank-$id", 1, notification)
    }
    fun cancel(context: android.content.Context, id: String) { context.getSystemService(NotificationManager::class.java).cancel("bank-$id", 1) }
}
