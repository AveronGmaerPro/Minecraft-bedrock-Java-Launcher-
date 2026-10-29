package com.my.launcher

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Bg = Color(0xFF1E1E1E)
val Panel = Color(0xFF2B2B2B)
val Green = Color(0xFF3C8527)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("launcher", Context.MODE_PRIVATE)

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        val totalGb = info.totalMem / 1024f / 1024f / 1024f
        val maxGb = (totalGb - 3f).coerceAtLeast(1.5f)

        setContent {
            var name by remember { mutableStateOf(prefs.getString("name", "") ?: "") }
            var edition by remember { mutableStateOf(prefs.getString("edition", "Java") ?: "Java") }
            var ram by remember { mutableStateOf(prefs.getFloat("ram", maxGb).coerceIn(1f, maxGb)) }

            if (name.isBlank()) {
                AccountScreen { n, e ->
                    prefs.edit().putString("name", n).putString("edition", e).apply()
                    name = n
                    edition = e
                }
            } else {
                MainScreen(
                    name = name,
                    edition = edition,
                    totalGb = totalGb,
                    maxGb = maxGb,
                    ram = ram,
                    onEdition = {
                        edition = if (edition == "Java") "Bedrock" else "Java"
                        prefs.edit().putString("edition", edition).apply()
                    },
                    onRam = {
                        ram = it
                        prefs.edit().putFloat("ram", it).apply()
                    }
                )
            }
        }
    }
}

@Composable
fun Box4(
    text: String,
    mod: Modifier = Modifier,
    color: Color = Panel,
    onClick: () -> Unit = {}
) {
    Box(
        mod.background(color)
            .border(2.dp, Color.Gray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) { Text(text, color = Color.White, fontSize = 14.sp) }
}

@Composable
fun AccountScreen(onDone: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var edition by remember { mutableStateOf("Java") }
    Column(
        Modifier.fillMaxSize().background(Bg).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Local Account", color = Color.White, fontSize = 24.sp)
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier.fillMaxWidth().height(52.dp)
                .background(Panel).border(2.dp, Color.Gray).padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (name.isEmpty()) Text("Apna naam likho", color = Color.Gray, fontSize = 16.sp)
            BasicTextField(
                value = name,
                onValueChange = { name = it.take(16) },
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Box4(
                "Java", Modifier.weight(1f).height(52.dp),
                color = if (edition == "Java") Green else Panel
            ) { edition = "Java" }
            Spacer(Modifier.width(8.dp))
            Box4(
                "Bedrock", Modifier.weight(1f).height(52.dp),
                color = if (edition == "Bedrock") Green else Panel
            ) { edition = "Bedrock" }
        }
        Spacer(Modifier.height(16.dp))
        Box4(
            "CONTINUE", Modifier.fillMaxWidth().height(56.dp),
            color = if (name.isNotBlank()) Green else Panel
        ) { if (name.isNotBlank()) onDone(name.trim(), edition) }
    }
}

@Composable
fun MainScreen(
    name: String,
    edition: String,
    totalGb: Float,
    maxGb: Float,
    ram: Float,
    onEdition: () -> Unit,
    onRam: (Float) -> Unit
) {
    val ctx = LocalContext.current
    var showSettings by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Bg).padding(12.dp)) {
        Row(Modifier.fillMaxWidth().height(52.dp), verticalAlignment = Alignment.CenterVertically) {
            Box4(name, Modifier.weight(1f).fillMaxHeight())
            Spacer(Modifier.width(8.dp))
            Box4(edition, Modifier.weight(1.5f).fillMaxHeight()) { onEdition() }
            Spacer(Modifier.width(8.dp))
            Box4("Setting", Modifier.weight(1f).fillMaxHeight()) { showSettings = !showSettings }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Box4("Custom Control", Modifier.fillMaxWidth().height(44.dp))
                Spacer(Modifier.height(8.dp))
                Box4("Mod Launcher", Modifier.fillMaxWidth().height(44.dp))
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Box4("File Option", Modifier.fillMaxWidth().height(44.dp))
                Spacer(Modifier.height(8.dp))
                Box4("Log Share", Modifier.fillMaxWidth().height(44.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier.weight(1f).fillMaxWidth().background(Panel).border(2.dp, Color.Gray).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showSettings) {
                Column {
                    Text("Phone RAM: %.1f GB".format(totalGb), color = Color.White, fontSize = 16.sp)
                    Text("Safe ke liye 3 GB phone ke liye chhodi gayi hai", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Java RAM: %.1f GB".format(ram), color = Color.White, fontSize = 18.sp)
                    Slider(value = ram, onValueChange = onRam, valueRange = 1f..maxGb)
                }
            } else {
                Text("LOGO YAHAN", color = Color.Gray)
            }
        }
        Spacer(Modifier.height(12.dp))
        Box4("Version: 1.26.3  (Vanilla / Fabric / Forge)", Modifier.fillMaxWidth().height(44.dp))
        Spacer(Modifier.height(8.dp))
        Box4("PLAY", Modifier.fillMaxWidth().height(60.dp), color = Green) {
            Toast.makeText(ctx, "$edition chalane ka code abhi baaki hai", Toast.LENGTH_SHORT).show()
        }
    }
}
