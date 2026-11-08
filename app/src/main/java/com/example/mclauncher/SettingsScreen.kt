package com.mcpemcje.launcher

import android.app.ActivityManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SBg = Color(0xFF07070A)
private val SPanel = Color(0xFF14141A)
private val SOrange = Color(0xFFFF6A00)
private val SMuted = Color(0xFF9A9A9A)

fun getTotalRamMb(context: Context): Int {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val info = ActivityManager.MemoryInfo()
    am.getMemoryInfo(info)
    return (info.totalMem / (1024L * 1024L)).toInt()
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    val totalMb = remember { getTotalRamMb(context) }
    val reserve = if (totalMb >= 7000) 3072 else totalMb / 2
    val maxMb = (((totalMb - reserve) / 256) * 256).coerceAtLeast(512)
    var ramMb by remember { mutableStateOf(prefs.getInt("ram_mb", maxMb).coerceIn(512, maxMb)) }

    BackHandler { onBack() }

    Column(Modifier.fillMaxSize().background(SBg).systemBarsPadding().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Text("Settings", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))

        Column(
            Modifier.fillMaxWidth().background(SPanel, RoundedCornerShape(12.dp)).padding(16.dp)
        ) {
            Text("Java RAM", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Phone ki RAM: ${"%.1f".format(totalMb / 1024f)} GB",
                color = SMuted, fontSize = 12.sp
            )
            Spacer(Modifier.height(12.dp))
            Text("${"%.2f".format(ramMb / 1024f)} GB", color = SOrange, fontSize = 28.sp, fontWeight = FontWeight.Bold)

            if (maxMb > 512) {
                Slider(
                    value = ramMb.toFloat(),
                    onValueChange = {
                        ramMb = (Math.round(it / 256f) * 256).coerceIn(512, maxMb)
                    },
                    onValueChangeFinished = { prefs.edit().putInt("ram_mb", ramMb).apply() },
                    valueRange = 512f..maxMb.toFloat(),
                    steps = (((maxMb - 512) / 256) - 1).coerceAtLeast(0),
                    colors = SliderDefaults.colors(
                        thumbColor = SOrange,
                        activeTrackColor = SOrange,
                        inactiveTrackColor = Color(0xFF2C2C2C)
                    )
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("0.50 GB", color = SMuted, fontSize = 11.sp)
                    Text("${"%.2f".format(maxMb / 1024f)} GB", color = SMuted, fontSize = 11.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text("Recommended: max se thoda kam rakhiye", color = SMuted, fontSize = 12.sp)
            } else {
                Text("Is phone me RAM kam hai, 0.5 GB hi chalega.", color = SMuted, fontSize = 12.sp)
            }
        }
    }
}
