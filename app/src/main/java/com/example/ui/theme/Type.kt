package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Google Fonts - Lalezar (Display, Buttons, Logos, Titles)
val LalezarFamily = FontFamily(
    Font(R.font.lalezar, FontWeight.Normal)
)

// Google Fonts - Vazirmatn (Body, Stats, Subtitles)
val VazirmatnFamily = FontFamily(
    Font(R.font.vazirmatn, FontWeight.Normal)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = LalezarFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        color = TitleGold
    ),
    displayMedium = TextStyle(
        fontFamily = LalezarFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        color = TitleGold
    ),
    titleLarge = TextStyle(
        fontFamily = LalezarFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        color = TitleGold
    ),
    titleMedium = TextStyle(
        fontFamily = LalezarFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        color = TitleGold
    ),
    bodyLarge = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = IvoryTextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = IvoryTextPrimary
    ),
    bodySmall = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = IvoryTextSecondary
    ),
    labelLarge = TextStyle(
        fontFamily = LalezarFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = TitleGold
    ),
    labelMedium = TextStyle(
        fontFamily = VazirmatnFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = IvoryTextSecondary
    )
)

/**
 * Converts numbers to Persian digits (e.g. 1234 -> ۱۲۳۴)
 */
fun Number.toPersianDigits(): String {
    val s = this.toString()
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder()
    for (c in s) {
        if (c in '0'..'9') sb.append(persianDigits[c - '0']) else sb.append(c)
    }
    return sb.toString()
}

/**
 * Converts string with Latin digits to Persian digits
 */
fun String.toPersianDigits(): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder()
    for (c in this) {
        if (c in '0'..'9') sb.append(persianDigits[c - '0']) else sb.append(c)
    }
    return sb.toString()
}

/**
 * Formats coin amounts into Persian format (e.g. 12500 -> ۱۲,۵۰۰)
 */
fun formatCoinsPersian(coins: Long): String {
    val formatted = "%,d".format(coins)
    return formatted.toPersianDigits()
}
