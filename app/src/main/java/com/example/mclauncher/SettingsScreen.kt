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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor

private val SBg = Color(0xFF0A0A0A)
private val SPanel = Color(0xFF161616)
private val SOrange = Color(0xFFFF6A00)
private val SMuted = Color(0xFF9A9A9A)

fun getTotalRamGb(context: Context): Int {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val info = ActivityManager.MemoryInfo()
    am.getMemoryInfo(info)
    val gb = info.totalMem / (1024.0 * 1024.0 * 1024.0)
    return Math.round(gb).toInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }

    val totalGb = remember { getTotalRamGb(context) }
    val maxGb = (totalGb - 3).coerceAtLeast(1)
    var ramGb by remember { mutableStateOf(prefs.getInt("ram_gb", 1).coerceIn(1, maxGb)) }

    BackHandler { onBack() }

    Column(Modifier.fillMaxSize().background(SBg).systemBarsPadding().padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Text("Settings", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))

        Column(
            Modifier.fillMaxWidth().background(SPanel, RoundedCornerShape(6.dp)).padding(16.dp)
        ) {
            Text("Java RAM", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Phone ki total RAM: $totalGb GB (3 GB system ke liye chhodi gayi)", color = SMuted, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            Text("$ramGb GB", color = SOrange, fontSize = 28.sp, fontWeight = FontWeight.Bold)

            if (maxGb > 1) {
                Slider(
                    value = ramGb.toFloat(),
                    onValueChange = { ramGb = floor(it).toInt().coerceIn(1, maxGb) },
                    onValueChangeFinished = { prefs.edit().putInt("ram_gb", ramGb).apply() },
                    valueRange = 1f..maxGb.toFloat(),
                    steps = (maxGb - 2).coerceAtLeast(0),
                    colors = SliderDefaults.colors(
                        thumbColor = SOrange,
                        activeTrackColor = SOrange,
                        inactiveTrackColor = Color(0xFF2C2C2C)
                    )
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("1 GB", color = SMuted, fontSize = 11.sp)
                    Text("$maxGb GB", color = SMuted, fontSize = 11.sp)
                }
            } else {
                Text("Is phone me RAM kam hai, 1 GB hi chalega.", color = SMuted, fontSize = 12.sp)
            }
        }
    }
}
