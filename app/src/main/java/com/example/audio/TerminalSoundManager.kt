package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Tactile Terminal Sound & Haptic FX Engine.
 * Synthesizes retro cyber terminal sounds on-the-fly without external audio assets.
 */
class TerminalSoundManager(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    // Sample rate for PCM audio
    private val sampleRate = 22050

    // Pre-synthesized PCM audio buffers for instantaneous playback
    private val keyClickBuffer: ByteArray by lazy { generateKeyClick() }
    private val beepBuffer: ByteArray by lazy { generateTone(1200.0, 50, 0.4) }
    private val accessGrantedBuffer: ByteArray by lazy { generateAccessGranted() }
    private val accessDeniedBuffer: ByteArray by lazy { generateAccessDenied() }
    private val sonarPingBuffer: ByteArray by lazy { generateSonarPing() }
    private val dataStreamBuffer: ByteArray by lazy { generateDataStream() }
    private val blackoutBuffer: ByteArray by lazy { generateBlackout() }
    private val phoneRingBuffer: ByteArray by lazy { generatePhoneRing() }
    private val radioStaticBuffer: ByteArray by lazy { generateRadioStatic() }
    private val bootBeepBuffer: ByteArray by lazy { generateBootBeep() }

    fun playPhoneRing() {
        if (!isSoundEnabled) return
        playPcmAsync(phoneRingBuffer)
        vibrateDouble()
    }

    fun playRadioStatic() {
        if (!isSoundEnabled) return
        playPcmAsync(radioStaticBuffer)
        vibrateLight()
    }

    fun playBootBeep() {
        if (!isSoundEnabled) return
        playPcmAsync(bootBeepBuffer)
        vibrateMedium()
    }

    fun playKeyClick() {
        if (!isSoundEnabled) return
        playPcmAsync(keyClickBuffer)
        vibrateLight()
    }

    fun playTerminalBeep() {
        if (!isSoundEnabled) return
        playPcmAsync(beepBuffer)
        vibrateMedium()
    }

    fun playAccessGranted() {
        if (!isSoundEnabled) return
        playPcmAsync(accessGrantedBuffer)
        vibrateDouble()
    }

    fun playAccessDenied() {
        if (!isSoundEnabled) return
        playPcmAsync(accessDeniedBuffer)
        vibrateHeavy()
    }

    fun playSonarPing() {
        if (!isSoundEnabled) return
        playPcmAsync(sonarPingBuffer)
        vibrateLight()
    }

    fun playDataStream() {
        if (!isSoundEnabled) return
        playPcmAsync(dataStreamBuffer)
    }

    fun playBlackout() {
        if (!isSoundEnabled) return
        playPcmAsync(blackoutBuffer)
        vibrateHeavy()
    }

    private fun playPcmAsync(buffer: ByteArray) {
        coroutineScope.launch {
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                // Auto release after playback
                coroutineScope.launch {
                    val durationMs = (buffer.size * 1000L) / sampleRate + 50
                    kotlinx.coroutines.delay(durationMs)
                    track.stop()
                    track.release()
                }
            } catch (_: Exception) {
                // Silently fallback if audio hardware is unavailable
            }
        }
    }

    // --- Sound Synthesis Algorithms ---

    private fun generateKeyClick(): ByteArray {
        val durationMs = 15
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 300.0)
            val freq = 2100.0 + (i % 3) * 200.0
            val sample = (sin(2.0 * PI * freq * t) * envelope * 120.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateTone(frequency: Double, durationMs: Int, volume: Double = 0.5): ByteArray {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.8 -> (numSamples - i) / (numSamples * 0.2)
                else -> 1.0
            }
            val sample = (sin(2.0 * PI * frequency * t) * envelope * volume * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateAccessGranted(): ByteArray {
        // High ascending cyber chime (880Hz -> 1320Hz -> 1760Hz)
        val durationMs = 180
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val freq = when {
                progress < 0.33 -> 880.0
                progress < 0.66 -> 1320.0
                else -> 1760.0
            }
            val envelope = (1.0 - progress * 0.7)
            val sample = (sin(2.0 * PI * freq * t) * envelope * 0.45 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateAccessDenied(): ByteArray {
        // Harsh discordant buzz (180Hz square-ish + 220Hz)
        val durationMs = 220
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq1 = 200.0
            val freq2 = 233.0
            val s1 = sin(2.0 * PI * freq1 * t)
            val s2 = sin(2.0 * PI * freq2 * t)
            val square = if ((s1 + s2) > 0) 0.5 else -0.5
            val envelope = exp(-t * 8.0)
            val sample = (square * envelope * 100.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateSonarPing(): ByteArray {
        // 1400Hz pure ping decaying over 400ms
        val durationMs = 400
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 9.0)
            val sample = (sin(2.0 * PI * 1350.0 * t) * envelope * 0.6 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateDataStream(): ByteArray {
        // Rapid digital packet chirp
        val durationMs = 120
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq = 1200.0 + ((i / 80) % 5) * 400.0
            val sample = (sin(2.0 * PI * freq * t) * 0.3 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateBlackout(): ByteArray {
        // Power-down pitch drop from 1600Hz down to 60Hz
        val durationMs = 500
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val freq = 1600.0 * (1.0 - progress) + 60.0
            val envelope = exp(-t * 4.0)
            val sample = (sin(2.0 * PI * freq * t) * envelope * 0.5 * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generatePhoneRing(): ByteArray {
        // Dual-tone DTMF telephone ring (440Hz + 480Hz)
        val durationMs = 600
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.8 -> (numSamples - i) / (numSamples * 0.2)
                else -> 1.0
            }
            val s1 = sin(2.0 * PI * 440.0 * t)
            val s2 = sin(2.0 * PI * 480.0 * t)
            val sample = ((s1 + s2) * 0.35 * envelope * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateRadioStatic(): ByteArray {
        // Police/wiretap radio noise burst with modulation
        val durationMs = 350
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        var lastVal = 0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 4.0)
            val rand = kotlin.random.Random.nextInt(-60, 61)
            lastVal = ((lastVal * 0.7) + (rand * 0.3)).toInt()
            val sample = (lastVal * envelope).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    private fun generateBootBeep(): ByteArray {
        // Futuristic ctOS resonant boot chord
        val durationMs = 700
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ByteArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 3.5)
            val s1 = sin(2.0 * PI * 330.0 * t)
            val s2 = sin(2.0 * PI * 660.0 * t)
            val sample = ((s1 * 0.4 + s2 * 0.3) * envelope * 127.0).toInt()
            buffer[i] = (128 + sample).coerceIn(0, 255).toByte()
        }
        return buffer
    }

    // --- Tactile Haptic Vibration Feedback ---

    private fun vibrateLight() {
        if (!isHapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(10)
                }
            }
        } catch (_: Exception) {}
    }

    private fun vibrateMedium() {
        if (!isHapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(25)
                }
            }
        } catch (_: Exception) {}
    }

    private fun vibrateDouble() {
        if (!isHapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 40), -1))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(longArrayOf(0, 30, 40, 40), -1)
                }
            }
        } catch (_: Exception) {}
    }

    private fun vibrateHeavy() {
        if (!isHapticsEnabled) return
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(80)
                }
            }
        } catch (_: Exception) {}
    }
}
