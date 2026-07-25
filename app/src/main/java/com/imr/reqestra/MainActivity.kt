package com.imr.reqestra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.imr.reqestra.presentation.ReqestraApp
import com.imr.reqestra.presentation.theme.ReqestraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ReqestraTheme { ReqestraApp() } }
    }
}
