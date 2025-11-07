package com.carbonera.motioncomm.presentation

import com.carbonera.motioncomm.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

@Composable
fun MotionHeatScreen(
    magnitude: Float,
    messageReceived: String,
    onSendClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val formattedMagnitude = String.format("%.2f", magnitude)

    val heatColor = remember(magnitude) {
        val norm = min(magnitude / 30f, 1f)
        Color(
            red = norm,
            green = 1f - norm,
            blue = (1f - norm) * 0.8f
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = heatColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo da UTFPR",
                modifier = Modifier.height(50.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aceleração: $formattedMagnitude m/s²",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Recebido: $messageReceived",
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                scope.launch {
                    val time = LocalTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    val message = "Movimento: $formattedMagnitude às $time"
                    onSendClick(message)
                }
            }) {
                Text("Enviar ao celular")
            }
        }
    }
}