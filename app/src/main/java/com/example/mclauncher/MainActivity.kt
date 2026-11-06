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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF121212)
private val Panel = Color(0xFF1E1E1E)
private val Green = Color(0xFF5D9B3A)
private val Shape = RoundedCornerShape(4.dp)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Green, background = Bg, surface = Panel)) {
                LauncherScreen()
            }
        }
    }
}

@Composable
fun LauncherScreen() {
    var isJava by remember { mutableStateOf(true) }
    var version by remember { mutableStateOf("1.26.3") }
    var loader by remember { mutableStateOf("Vanilla") }
    var menuOpen by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }

    val loaders = if (isJava) listOf("Vanilla", "Fabric", "Forge") else listOf("Release", "Preview")

    Column(
        Modifier.fillMaxSize().background(Bg).systemBarsPadding().padding(12.dp)
    ) {
        // TOP BAR
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier.border(BorderStroke(1.dp, Green), Shape)
                    .clickable { msg = "Profile (jald aa raha hai)" }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, null, tint = Green)
                Spacer(Modifier.width(6.dp))
                Text("Averon", color = Color.White, fontSize = 14.sp)
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.border(BorderStroke(1.dp, Green), Shape)) {
                ModeTab("Java", isJava) { isJava = true; loader = "Vanilla" }
                ModeTab("Bedrock", !isJava) { isJava = false; loader = "Release" }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { msg = "Settings (jald aa raha hai)" }) {
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
                    .background(Panel, Shape),
                contentAlignment = Alignment.Center
            ) {
                Text("MCPE & MCJE", color = Green, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SideButton(Icons.Default.Folder, "Files") { msg = "File option" }
                SideButton(Icons.Default.Share, "Log") { msg = "Log file share" }
            }
        }

        Spacer(Modifier.height(12.dp))

        // VERSION ROW
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory2, null, tint = Green)
            Spacer(Modifier.width(8.dp))
            Text(version, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Box {
                Row(
                    Modifier.border(BorderStroke(1.dp, Green), Shape)
                        .clickable { menuOpen = true }.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(loader, color = Color.White)
                    Icon(Icons.Default.ArrowDropDown, null, tint = Green)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    loaders.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { loader = it; menuOpen = false })
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // PLAY
        Button(
            onClick = { msg = "PLAY dabaya: ${if (isJava) "Java" else "Bedrock"} $version ($loader)" },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = Shape,
            colors = ButtonDefaults.buttonColors(containerColor = Green)
        ) {
            Text("PLAY", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { 0f }, modifier = Modifier.fillMaxWidth(), color = Green)
        Text("Loading file size", color = Color.Gray, fontSize = 11.sp)
        if (msg.isNotEmpty()) Text(msg, color = Green, fontSize = 12.sp)
    }
}

@Composable
fun ModeTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.background(if (selected) Green else Color.Transparent)
            .clickable { onClick() }.padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SideButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        Modifier.size(64.dp).background(Panel, Shape)
            .border(BorderStroke(1.dp, Green), Shape)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, label, tint = Green)
        Text(label, color = Color.White, fontSize = 10.sp)
    }
}
