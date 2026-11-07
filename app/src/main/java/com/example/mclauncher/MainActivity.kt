package com.mcpemcje.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0A0A0A)
private val Panel = Color(0xFF161616)
private val Panel2 = Color(0xFF101010)
private val Orange = Color(0xFFFF6A00)
private val OrangeLight = Color(0xFFFF8A33)
private val OrangeDark = Color(0xFFE05000)
private val Line = Color(0xFF2C2C2C)
private val Muted = Color(0xFF9A9A9A)
private val Shape = RoundedCornerShape(6.dp)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Orange, background = Bg, surface = Panel, onSurface = Color.White
                )
            ) {
                var showSettings by remember { mutableStateOf(false) }
                if (showSettings) {
                    SettingsScreen(onBack = { showSettings = false })
                } else {
                    LauncherScreen(onOpenSettings = { showSettings = true })
                }
            }
        }
    }
}

@Composable
fun LauncherScreen(onOpenSettings: () -> Unit) {
    var isJava by remember { mutableStateOf(true) }
    var version by remember { mutableStateOf("1.26.3") }
    var loader by remember { mutableStateOf("Vanilla") }
    var menuOpen by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }

    val loaders = if (isJava) listOf("Vanilla", "Fabric", "Forge") else listOf("Release", "Preview")

    Column(
        Modifier.fillMaxSize().background(Bg).systemBarsPadding().padding(14.dp)
    ) {
        // TOP BAR
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier.background(Panel, Shape)
                    .border(BorderStroke(1.dp, Line), Shape)
                    .clickable { msg = "Profile (jald aa raha hai)" }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, null, tint = Orange)
                Spacer(Modifier.width(6.dp))
                Text("Averon", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.background(Panel, Shape).border(BorderStroke(1.dp, Line), Shape)) {
                ModeTab("Java", isJava) { isJava = true; loader = "Vanilla" }
                ModeTab("Bedrock", !isJava) { isJava = false; loader = "Release" }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { onOpenSettings() }) {
                Icon(Icons.Default.Settings, "Settings", tint = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        // MIDDLE
        Row(Modifier.weight(1f).fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SideButton(Icons.Default.Gamepad, "Control") { msg = "Custom Control" }
                SideButton(Icons.Default.Tune, "Custom") { msg = "Custom Control 2" }
            }
            Box(
                Modifier.weight(1f).fillMaxHeight().padding(horizontal = 10.dp)
                    .background(Brush.verticalGradient(listOf(Panel, Panel2)), Shape)
                    .border(BorderStroke(1.dp, Line), Shape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MCPE & MCJE", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Box(Modifier.width(40.dp).height(3.dp).background(Orange))
                    Spacer(Modifier.height(6.dp))
                    Text("LAUNCHER", color = Orange, fontSize = 12.sp, letterSpacing = 5.sp, fontWeight = FontWeight.Medium)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SideButton(Icons.Default.Folder, "Files") { msg = "File option" }
                SideButton(Icons.Default.Share, "Log") { msg = "Log file share" }
            }
        }

        Spacer(Modifier.height(14.dp))

        // VERSION ROW
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory2, null, tint = Orange)
            Spacer(Modifier.width(8.dp))
            Text(version, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Box {
                Row(
                    Modifier.background(Panel, Shape)
                        .border(BorderStroke(1.dp, Orange), Shape)
                        .clickable { menuOpen = true }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(loader, color = Color.White)
                    Icon(Icons.Default.ArrowDropDown, null, tint = Orange)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    loaders.forEach {
                        DropdownMenuItem(
                            text = { Text(it, color = Color.White) },
                            onClick = { loader = it; menuOpen = false }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // PLAY
        Box(
            Modifier.fillMaxWidth().height(58.dp)
                .background(Brush.horizontalGradient(listOf(OrangeDark, OrangeLight)), Shape)
                .clickable { msg = "PLAY dabaya: ${if (isJava) "Java" else "Bedrock"} $version ($loader)" },
            contentAlignment = Alignment.Center
        ) {
            Text("PLAY", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, letterSpacing = 4.sp)
        }

        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { 0f },
            modifier = Modifier.fillMaxWidth(),
            color = Orange,
            trackColor = Line
        )
        Spacer(Modifier.height(4.dp))
        Text("Loading file size", color = Muted, fontSize = 11.sp)
        if (msg.isNotEmpty()) Text(msg, color = Orange, fontSize = 12.sp)
    }
}

@Composable
fun ModeTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.background(if (selected) Orange else Color.Transparent, Shape)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, color = if (selected) Color.Black else Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SideButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        Modifier.size(64.dp)
            .background(Panel, Shape)
            .border(BorderStroke(1.dp, Line), Shape)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, label, tint = Orange)
        Text(label, color = Color.White, fontSize = 10.sp)
    }
}
