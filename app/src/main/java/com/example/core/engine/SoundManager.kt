package com.example.core.engine

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null

    init {
        runCatching {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        }
    }

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playCardSound(soundEnabled: Boolean) {
        if (!soundEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        }
    }

    fun playButtonClick(soundEnabled: Boolean) {
        if (!soundEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 30)
        }
    }

    fun playTrumpSelected(soundEnabled: Boolean) {
        if (!soundEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 120)
        }
    }

    fun playTrickWon(soundEnabled: Boolean) {
        if (!soundEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
        }
    }

    fun playGameWon(soundEnabled: Boolean) {
        if (!soundEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
        }
    }

    fun playTurnAlert(soundEnabled: Boolean) {
        if (!soundEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 80)
        }
    }

    fun vibrate(vibrationEnabled: Boolean, durationMs: Long = 40L) {
        if (!vibrationEnabled) return
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        }
    }
}
