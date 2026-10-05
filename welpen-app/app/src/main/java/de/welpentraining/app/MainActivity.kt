package de.welpentraining.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.welpentraining.app.ui.WelpenApp
import de.welpentraining.app.ui.theme.WelpenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WelpenTheme {
                WelpenApp()
            }
        }
    }
}
