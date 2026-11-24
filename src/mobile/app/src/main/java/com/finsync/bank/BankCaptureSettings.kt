package com.finsync.bank

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

data class CaptureConfig(val workspace: String = "device", val enabled: Boolean = false,
    val packages: Set<String> = emptySet(), val enabledAt: Long = 0)
class BankCaptureSettings(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("bank-capture-consent", Context.MODE_PRIVATE)
    fun read(): CaptureConfig {
        // A single snapshot prevents mixed owner/allowlist values across a consent change.
        val values = prefs.all
        return CaptureConfig(values["workspace"] as? String ?: "device", values["enabled"] == true,
            (values["packages"] as? Set<*>)?.filterIsInstance<String>()?.toSet().orEmpty(), values["enabledAt"] as? Long ?: 0)
    }
    @Synchronized fun save(config: CaptureConfig) {
        check(prefs.edit().putString("workspace", config.workspace).putBoolean("enabled", config.enabled)
            .putStringSet("packages", config.packages).putLong("enabledAt", config.enabledAt).commit())
    }
    fun pauseForDifferentWorkspace(workspace: String) { val config = read(); if (config.workspace != workspace && config.enabled) save(config.copy(enabled = false)) }
    fun hasCaptureError(workspace: String) = prefs.getString("captureError", null) == workspace
    fun reportCaptureError(workspace: String) { prefs.edit().putString("captureError", workspace).commit() }
    fun clearCaptureError() { prefs.edit().remove("captureError").apply() }
    // Source app is only a suggestion: one bank app can contain several accounts.
    // Hash owner and package together so defaults never cross account workspaces.
    private fun walletKey(workspace: String, sourcePackage: String) = "wallet-${digest("$workspace\u0000$sourcePackage") }"
    fun defaultWallet(workspace: String, sourcePackage: String): String? =
        prefs.getString(walletKey(workspace, sourcePackage), null)
    fun setDefaultWallet(workspace: String, sourcePackage: String, walletId: String?) {
        val editor = prefs.edit()
        val key = walletKey(workspace, sourcePackage)
        if (walletId == null) editor.remove(key) else editor.putString(key, walletId)
        check(editor.commit()) { "Couldn't remember this wallet. Please try again." }
    }
    fun observeChanges(onChange: () -> Unit): () -> Unit {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> onChange() }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        return { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    companion object {
        fun hasAccess(context: Context): Boolean = if (android.os.Build.VERSION.SDK_INT >= 27)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager)
                .isNotificationListenerAccessGranted(ComponentName(context, BankNotificationListener::class.java))
            else Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
                ?.split(':')?.contains(ComponentName(context, BankNotificationListener::class.java).flattenToString()) == true
    }
}
