/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.carbonera.motioncomm.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.sqrt

class WearMainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var messageClient: MessageClient

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val _accelMagnitude = mutableStateOf(0f)
    val accelMagnitude: State<Float> get() = _accelMagnitude

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        messageClient = Wearable.getMessageClient(this)

        setContent {
            val context = LocalContext.current
            var messageReceived by remember { mutableStateOf("Aguardando mensagem...") }

            DisposableEffect(Unit) {
                val messageClient = Wearable.getMessageClient(context)
                val listener = MessageClient.OnMessageReceivedListener { messageEvent: MessageEvent ->
                    if (messageEvent.path == "/msg") {
                        val received = String(messageEvent.data)
                        Log.d("Wear", "Recebido: $received")
                        messageReceived = received
                    }
                }
                messageClient.addListener(listener)
                onDispose {
                    messageClient.removeListener(listener)
                }
            }

            MotionHeatScreen(
                magnitude = accelMagnitude.value,
                messageReceived = messageReceived,
                onSendClick = { enviarMensagemAoMobile(it, this) }
            )
        }

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            Log.d("MotionService", "Acelerômetro iniciado.")
        } ?: Log.e("MotionService", "Acelerômetro não disponível.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val (x, y, z) = event?.values ?: return
        val magnitude = sqrt(x * x + y * y + z * z)
        _accelMagnitude.value = magnitude
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun enviarMensagemAoMobile(msg: String, context: Context) {
        coroutineScope.launch {
            val nodeClient = Wearable.getNodeClient(context)
            val nodes = nodeClient.connectedNodes.await()
            for (node in nodes) {
                messageClient.sendMessage(node.id, "/msg", msg.toByteArray())
            }
        }
    }
}