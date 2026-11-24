package com.finsync.ui;

/** Test APK only: real Android notification source, no bank account or payment. */
public class BankFixtureActivity extends android.app.Activity {
    @Override public void onCreate(android.os.Bundle saved) {
        super.onCreate(saved);
        android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
        if (getIntent().getBooleanExtra("clear", false)) { manager.cancelAll(); finish(); return; }
        manager.createNotificationChannel(new android.app.NotificationChannel("qa-bank", "QA bank fixture", android.app.NotificationManager.IMPORTANCE_DEFAULT));
        String text = new String(android.util.Base64.decode(getIntent().getStringExtra("text64"), android.util.Base64.DEFAULT), java.nio.charset.StandardCharsets.UTF_8);
        android.app.Notification notification = new android.app.Notification.Builder(this, "qa-bank")
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Bank QA fixture")
            .setContentText(text)
            .setWhen(getIntent().getLongExtra("eventTime", System.currentTimeMillis())).build();
        manager.notify(getIntent().getStringExtra("tag"), 1, notification);
        android.util.Log.i("FinSyncBankQA", "Posted fixture: characters=" + text.length() + ", notificationsAllowed=" + manager.areNotificationsEnabled());
        finish();
    }
}
