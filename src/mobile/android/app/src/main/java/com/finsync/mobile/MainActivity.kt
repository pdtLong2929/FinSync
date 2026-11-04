package com.finsync.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.finsync.mobile.ui.FinSyncNavGraph
import com.finsync.mobile.ui.theme.FinSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinSyncTheme {
                FinSyncNavGraph()
            }
        }
    }
}
