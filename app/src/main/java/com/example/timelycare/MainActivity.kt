package com.example.timelycare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.timelycare.ui.theme.TimelyCareTheme
import com.example.timelycare.ui.navigation.TimelyCareApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimelyCareTheme {
                TimelyCareApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimelyCareAppPreview() {
    TimelyCareTheme {
        TimelyCareApp()
    }
}