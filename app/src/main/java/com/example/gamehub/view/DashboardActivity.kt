package com.example.gamehub.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as? Activity

    Text(
        text = "Dashboard",
        color = Color.Black
    )
    IconButton(
        onClick = {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            activity?.finish()
        },
        modifier = Modifier
            .size(40.dp)
            .background(
                color = Color.White.copy(alpha = 0.05f),
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.1f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Back",
            tint = Color.Black
        )
    }

}

@Preview(showBackground = true)
@Composable
fun DashPreview() {

}