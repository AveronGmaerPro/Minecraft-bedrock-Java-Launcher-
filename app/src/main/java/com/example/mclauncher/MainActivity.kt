package com.mcpemcje.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Bg = Color(0xFF07070A)
private val Card = Color(0xFF14141A)
private val Card2 = Color(0xFF0E0E12)
private val Orange = Color(0xFFFF6A00)
private val OrangeLight = Color(0xFFFF8A33)
private val OrangeDark = Color(0xFFE05000)
private val Line = Color(0xFF26262E)
private val Muted = Color(0xFF9A9AA5)
private val Round = RoundedCornerShape(12.dp)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Orange, background = Bg, surface = Card, onSurface = Color.White
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
    var progress by remember { mutableStateOf(0f) }
    var running by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val loaders = if (isJava) listOf("Vanilla", "Fabric", "Forge") else listOf("Release", "Preview")
    val versions = listOf("1.26.3", "1.21.4", "1.20.1", "1.19.4", "1.16.5", "1.12.2")

    Column(
        Modifier.fillMaxSize().background(Bg).systemBarsPadding().padding(16.dp)
    ) {
        // TOP BAR
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier
                    .background(Card, Round)
                    .border(BorderStroke(1.dp, Line), Round)
                    .clickable { msg = "Profile (jald aa raha hai)" }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, null, tint = Orange)
                Spacer(Modifier.width(8.dp))
                Text("Averon", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.weight(1f))
            ModeSwitch(isJava) { java ->
                isJava = java
                loader = if (java) "Vanilla" else "Release"
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, "Settings", tint = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

        // MIDDLE
        Row(Modifier.weight(1f).fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SideButton(Icons.Default.Gamepad, "Control") { msg = "Custom Control" }
                SideButton(Icons.Default.Tune, "Custom") { msg = "Custom Control 2" }
            }
            HeroCard(
                isJava = isJava,
                version = version,
                modifier = Modifier.weight(1f).fillMaxHeight().padding(horizontal = 12.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SideButton(Icons.Default.Folder, "Files") { msg = "File option" }
                SideButton(Icons.Default.Share, "Log") { msg = "Log file share" }
            }
        }

        Spacer(Modifier.height(16.dp))

        // VERSION CARDS
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(versions) { v ->
                val selected = v == version
                val borderColor by animateColorAsState(if (selected) Orange else Line, label = "vb")
                Box(
                    Modifier
                        .background(if (selected) Orange.copy(alpha = 0.12f) else Card, Round)
                        .border(BorderStroke(1.dp, borderColor), Round)
                        .clickable { version = v }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        v,
                        color = if (selected) Orange else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // LOADER ROW
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory2, null, tint = Orange)
            Spacer(Modifier.width(8.dp))
            Text("Type", color = Muted, fontSize = 14.sp)
            Spacer(Modifier.weight(1f))
            Box {
                Row(
                    Modifier
                        .background(Card, Round)
                        .border(BorderStroke(1.dp, Line), Round)
                        .clickable { menuOpen = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(loader, color = Color.White)
                    Icon(Icons.Default.ArrowDropDown, null, tint = Orange)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    loaders.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { loader = it; menuOpen = false }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // PLAY BUTTON (progress andar hi bharta hai)
        val interaction = remember { MutableInteractionSource() }
        val pressed by interaction.collectIsPressedAsState()
        val scale by animateFloatAsState(if (pressed) 0.96f else 1f, label = "playScale")
        Box(
            Modifier
                .fillMaxWidth()
                .height(58.dp)
                .scale(scale)
                .shadow(14.dp, Round, ambientColor = Orange, spotColor = Orange)
                .clip(Round)
                .background(if (running) Card else Color.Transparent)
                .background(
                    if (running) Brush.horizontalGradient(listOf(Card, Card))
                    else Brush.horizontalGradient(listOf(OrangeDark, OrangeLight))
                )
                .clickable(interactionSource = interaction, indication = null) {
                    if (!running) {
                        running = true
                        scope.launch {
                            for (i in 0..100) {
                                progress = i / 100f
                                delay(30)
                            }
                            running = false
                            progress = 0f
                            msg = "Demo poora: ${if (isJava) "Java" else "Bedrock"} $version ($loader)"
                        }
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            if (running) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(Brush.horizontalGradient(listOf(OrangeDark, OrangeLight)))
                )
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (running) "${(progress * 100).toInt()}%" else "PLAY",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = if (running) Color.White else Color.Black,
                    letterSpacing = 5.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            if (msg.isEmpty()) "Ready" else msg,
            color = if (msg.isEmpty()) Muted else Orange,
            fontSize = 12.sp
        )
    }
}

@Composable
fun HeroCard(isJava: Boolean, version: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(Round)
            .background(Brush.radialGradient(listOf(Orange.copy(alpha = 0.28f), Card2, Bg), radius = 700f))
            .border(BorderStroke(1.dp, Line), Round),
        contentAlignment = Alignment.Center
    ) {
        // Minecraft jaisa block pattern (image ki jagah)
        Canvas(Modifier.matchParentSize()) {
            val cell = 28.dp.toPx()
            val cols = (size.width / cell).toInt() + 1
            val rows = (size.height / cell).toInt() + 1
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val a = ((r * 37 + c * 91 + r * c) % 7) / 7f * 0.07f
                    drawRect(
                        color = Color.White.copy(alpha = a),
                        topLeft = Offset(c * cell + 1f, r * cell + 1f),
                        size = Size(cell - 2f, cell - 2f)
                    )
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CubeLogo(Modifier.size(84.dp))
            Spacer(Modifier.height(14.dp))
            Text("MC Launcher", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Box(Modifier.width(40.dp).height(3.dp).background(Orange, Round))
            Spacer(Modifier.height(10.dp))
            Text(
                "${if (isJava) "Java" else "Bedrock"}  •  $version",
                color = Muted, fontSize = 13.sp, letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun CubeLogo(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        fun p(x: Float, y: Float) = Offset(x * w, y * h)

        val top = Path().apply {
            moveTo(p(0.5f, 0.05f).x, p(0.5f, 0.05f).y)
            lineTo(p(0.95f, 0.28f).x, p(0.95f, 0.28f).y)
            lineTo(p(0.5f, 0.5f).x, p(0.5f, 0.5f).y)
            lineTo(p(0.05f, 0.28f).x, p(0.05f, 0.28f).y)
            close()
        }
        val left = Path().apply {
            moveTo(p(0.05f, 0.28f).x, p(0.05f, 0.28f).y)
            lineTo(p(0.5f, 0.5f).x, p(0.5f, 0.5f).y)
            lineTo(p(0.5f, 0.95f).x, p(0.5f, 0.95f).y)
            lineTo(p(0.05f, 0.72f).x, p(0.05f, 0.72f).y)
            close()
        }
        val right = Path().apply {
            moveTo(p(0.95f, 0.28f).x, p(0.95f, 0.28f).y)
            lineTo(p(0.5f, 0.5f).x, p(0.5f, 0.5f).y)
            lineTo(p(0.5f, 0.95f).x, p(0.5f, 0.95f).y)
            lineTo(p(0.95f, 0.72f).x, p(0.95f, 0.72f).y)
            close()
        }
        drawPath(top, OrangeLight)
        drawPath(left, Orange)
        drawPath(right, OrangeDark)
    }
}

@Composable
fun ModeSwitch(isJava: Boolean, onChange: (Boolean) -> Unit) {
    val offset by animateDpAsState(if (isJava) 0.dp else 82.dp, label = "sw")
    Box(
        Modifier
            .background(Card, Round)
            .border(BorderStroke(1.dp, Line), Round)
            .padding(3.dp)
    ) {
        Box(
            Modifier
                .offset(x = offset)
                .width(82.dp)
                .height(36.dp)
                .background(Orange, RoundedCornerShape(9.dp))
        )
        Row {
            Box(
                Modifier.width(82.dp).height(36.dp).clickable { onChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text("Java", color = if (isJava) Color.Black else Color.White, fontWeight = FontWeight.Bold)
            }
            Box(
                Modifier.width(82.dp).height(36.dp).clickable { onChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text("Bedrock", color = if (!isJava) Color.Black else Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SideButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        Modifier
            .size(64.dp)
            .background(Card, Round)
            .border(BorderStroke(1.dp, Line), Round)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, label, tint = Orange)
        Text(label, color = Color.White, fontSize = 10.sp)
    }
}
