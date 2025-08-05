package com.sharesplit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sharesplit.app.ui.screens.SimpleTestScreen
import com.sharesplit.app.ui.theme.ShareSplitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            ShareSplitTheme {
                SimpleTestScreen()
            }
        }
    }
}