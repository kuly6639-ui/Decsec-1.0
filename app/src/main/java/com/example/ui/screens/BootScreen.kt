package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TerminalSoundManager
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.DedsecPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.TerminalBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class BootPhase {
    INIT_BLACK,
    MINIMAL_X,
    SYSTEM_LOADING,
    HEXAGON_CIPHER,
    CTOS_BEAM_LOGO,
    ISOMETRIC_EMBLEM,
    DEDSEC_HIJACK_BOXES,
    BOOT_COMPLETE
}

/**
 * Watch Dogs ctOS Boot Animation matching the user's video,
 * concluding with the DedSec boxed letter-by-letter decryption sequence.
 */
@Composable
fun BootScreen(
    soundManager: TerminalSoundManager,
    onBootFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableStateOf(BootPhase.MINIMAL_X) }
    var cipherString by remember { mutableStateOf("Lxrqgeukf") }

    // Target boxed letters sequence: D E D S E C / c T O S
    val targetWords = listOf(
        listOf("D", "E", "D", "S", "E", "C"),
        listOf("c", "T", "O", "S", "2", ".", "0")
    )

    // Revealed index counter for the boxed letters
    var revealedCount by remember { mutableIntStateOf(0) }
    val totalLetters = targetWords.sumOf { it.size }

    // Scramble character randomizer ticker
    val cipherCharset = "0123456789ABCDEF$#@%&*?><Ø§"

    LaunchedEffect(Unit) {
        soundManager.playBootBeep()

        // Phase 1: Minimalist 'X' (approx 1.8s)
        phase = BootPhase.MINIMAL_X
        delay(1800)

        // Phase 2: System Loading stepped bars (approx 2s)
        phase = BootPhase.SYSTEM_LOADING
        soundManager.playTerminalBeep()
        delay(2000)

        // Phase 3: Hexagon Cipher text (approx 2s)
        phase = BootPhase.HEXAGON_CIPHER
        soundManager.playDataStream()
        repeat(8) {
            val randomChars = (1..9).map { cipherCharset.random() }.joinToString("")
            cipherString = randomChars
            delay(220)
        }

        // Phase 4: Horizontal beam + "ctOS" logo (approx 2.5s)
        phase = BootPhase.CTOS_BEAM_LOGO
        soundManager.playBootBeep()
        delay(2600)

        // Phase 5: Isometric Wireframe Polyhedron in circle (approx 2.2s)
        phase = BootPhase.ISOMETRIC_EMBLEM
        soundManager.playSonarPing()
        delay(2200)

        // Phase 6: DedSec Hijack & Boxed Letters Decryption
        phase = BootPhase.DEDSEC_HIJACK_BOXES
        soundManager.playAccessGranted()

        // Reveal letters in boxes one by one with tactile sound
        for (i in 1..totalLetters) {
            delay(160)
            revealedCount = i
            soundManager.playKeyClick()
        }

        delay(1200)
        phase = BootPhase.BOOT_COMPLETE
        onBootFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("boot_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Skip Button in Top-Right
        Surface(
            color = Color.Black.copy(alpha = 0.7f),
            shape = CutCornerShape(4.dp),
            border = BorderStroke(1.dp, TextMuted),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 20.dp)
                .clickable { onBootFinished() }
                .testTag("skip_boot_button")
        ) {
            Text(
                text = "SKIP [>>]",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        when (phase) {
            BootPhase.INIT_BLACK -> {
                // Black screen
            }

            BootPhase.MINIMAL_X -> {
                // Video Frame 00:01: Clean white 'X'
                Canvas(modifier = Modifier.size(90.dp)) {
                    val w = size.width
                    val h = size.height
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(w, h),
                        strokeWidth = 2.2f
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, h),
                        end = Offset(w, 0f),
                        strokeWidth = 2.2f
                    )
                }
            }

            BootPhase.SYSTEM_LOADING -> {
                // Video Frame 00:03: Stepped white rectangular bars + "System Loading"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Surface(
                            color = Color.White,
                            modifier = Modifier
                                .width(28.dp)
                                .height(12.dp)
                        ) {}
                        Spacer(modifier = Modifier.height(3.dp))
                        Surface(
                            color = Color.White,
                            modifier = Modifier
                                .width(56.dp)
                                .height(12.dp)
                        ) {}
                        Spacer(modifier = Modifier.height(3.dp))
                        Surface(
                            color = Color.White,
                            modifier = Modifier
                                .width(88.dp)
                                .height(12.dp)
                        ) {}
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "System Loading",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            BootPhase.HEXAGON_CIPHER -> {
                // Video Frame 00:05: Wireframe irregular polygon / hexagon with cipher text
                Box(
                    modifier = Modifier.size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width * 0.45f
                        val path = Path()
                        val sides = 6
                        for (i in 0 until sides) {
                            val angle = Math.toRadians((i * (360.0 / sides) - 15.0)).toDouble()
                            val x = center.x + (radius * cos(angle)).toFloat()
                            val y = center.y + (radius * sin(angle)).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()
                        drawPath(path = path, color = Color.White, style = Stroke(width = 2f))
                    }

                    Text(
                        text = cipherString,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            BootPhase.CTOS_BEAM_LOGO -> {
                // Video Frame 00:09: Horizontal light beam + "ct OS" + "ctOS Authorized Device"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Glowing beam extending from left edge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .height(44.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.White,
                                        Color.White.copy(alpha = 0.9f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .padding(start = 130.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "CT",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = Color.White,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "OS",
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        color = Color.White,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 58.sp,
                                        letterSpacing = (-1).sp
                                    )
                                )
                            }
                            Text(
                                text = "ctOS Authorized Device",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            BootPhase.ISOMETRIC_EMBLEM -> {
                // Video Frame 00:11: Circle with 3D wireframe isometric polyhedron
                Box(
                    modifier = Modifier.size(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val outerRadius = size.width * 0.46f

                        // Outer thin circle
                        drawCircle(color = Color.White, radius = outerRadius, center = center, style = Stroke(width = 1.8f))

                        // Inner isometric cube / polyhedron lines
                        val r = outerRadius * 0.58f
                        val top = Offset(center.x, center.y - r)
                        val bottom = Offset(center.x, center.y + r)
                        val leftTop = Offset(center.x - r * 0.866f, center.y - r * 0.5f)
                        val leftBottom = Offset(center.x - r * 0.866f, center.y + r * 0.5f)
                        val rightTop = Offset(center.x + r * 0.866f, center.y - r * 0.5f)
                        val rightBottom = Offset(center.x + r * 0.866f, center.y + r * 0.5f)

                        // Outer hexagon boundary of cube
                        val hexPath = Path().apply {
                            moveTo(top.x, top.y)
                            lineTo(rightTop.x, rightTop.y)
                            lineTo(rightBottom.x, rightBottom.y)
                            lineTo(bottom.x, bottom.y)
                            lineTo(leftBottom.x, leftBottom.y)
                            lineTo(leftTop.x, leftTop.y)
                            close()
                        }
                        drawPath(hexPath, color = Color.White, style = Stroke(width = 1.8f))

                        // Internal Y axes
                        drawLine(color = Color.White, start = center, end = top, strokeWidth = 1.8f)
                        drawLine(color = Color.White, start = center, end = leftBottom, strokeWidth = 1.8f)
                        drawLine(color = Color.White, start = center, end = rightBottom, strokeWidth = 1.8f)

                        // Internal diagonals
                        drawLine(color = Color.White, start = leftTop, end = center, strokeWidth = 1.2f)
                        drawLine(color = Color.White, start = rightTop, end = center, strokeWidth = 1.2f)
                        drawLine(color = Color.White, start = bottom, end = center, strokeWidth = 1.2f)
                    }
                }
            }

            BootPhase.DEDSEC_HIJACK_BOXES, BootPhase.BOOT_COMPLETE -> {
                // DedSec Hijack sequence: Boxed letters appear letter-by-letter!
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "[!] cTOS 2.0 PROTOCOL HIJACKED",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = DedsecPink,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "DEDSEC OPERATIVE SYSTEM READY",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Row 1: [D][E][D][S][E][C]
                    var globalLetterCounter = 0
                    targetWords.forEachIndexed { wordIdx, wordLetters ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            wordLetters.forEach { letter ->
                                val letterGlobalIndex = globalLetterCounter
                                globalLetterCounter++

                                val isRevealed = letterGlobalIndex < revealedCount
                                val isCurrentlyScrambling = letterGlobalIndex == revealedCount

                                val boxChar = when {
                                    isRevealed -> letter
                                    isCurrentlyScrambling -> cipherCharset.random().toString()
                                    else -> " "
                                }

                                val boxBorderColor = when {
                                    isRevealed -> NeonGreen
                                    isCurrentlyScrambling -> DedsecPink
                                    else -> TerminalBorder
                                }

                                val boxBgColor = when {
                                    isRevealed -> CyberBlack
                                    isCurrentlyScrambling -> DedsecPink.copy(alpha = 0.2f)
                                    else -> Color(0xFF0A0F14)
                                }

                                val boxTextColor = when {
                                    isRevealed -> NeonGreenBright
                                    isCurrentlyScrambling -> DedsecPink
                                    else -> Color.Transparent
                                }

                                Surface(
                                    color = boxBgColor,
                                    shape = CutCornerShape(4.dp),
                                    border = BorderStroke(1.5.dp, boxBorderColor),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = boxChar,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                color = boxTextColor,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 20.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // Touch to proceed banner once all letters resolved
                    if (revealedCount >= totalLetters) {
                        Surface(
                            color = NeonGreen.copy(alpha = 0.15f),
                            shape = CutCornerShape(4.dp),
                            border = BorderStroke(1.dp, NeonGreen),
                            modifier = Modifier
                                .clickable { onBootFinished() }
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "INITIALIZATION COMPLETE // TOUCH TO ENTER",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = NeonGreenBright,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
