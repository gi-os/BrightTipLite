package com.gios.brighttiplite.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.thelightphone.sdk.ui.LightThemeTokens
import com.thelightphone.sdk.ui.lightClickable

/**
 * POS-style keypad, ported from BrightTip. Digits push in from the right, so 1-2-3-4
 * reads as $12.34. There is no decimal point to mistype.
 *
 * It takes whatever height is left under the figures, and the numerals are sized from
 * that height rather than fixed. The space left over differs a lot between the tip
 * screen and the custom percent screen, and a fixed size looks lost in one of them.
 * Both ends are clamped: the floor keeps digits readable, the ceiling stops a big
 * empty screen from drawing a numeral so large it reads as an error.
 */
@Composable
fun ColumnScope.Keypad(
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "DEL"),
    )
    BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
        // Height, because height is the scarce one: the keypad is always three keys across.
        val keyHeight = maxHeight.value / rows.size
        val digit = (keyHeight * DIGIT_OF_KEY).coerceIn(DIGIT_MIN, DIGIT_MAX).sp
        // C and DEL are words. Set at the digit size they would be wider than the key.
        val word = (digit.value * WORD_OF_DIGIT).coerceAtLeast(WORD_MIN).sp
        Column(Modifier.fillMaxSize()) {
            rows.forEach { row ->
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    row.forEach { key ->
                        Key(key, digitSize = digit, wordSize = word) {
                            when (key) {
                                "C" -> onClear()
                                "DEL" -> onBackspace()
                                else -> onDigit(key.toInt())
                            }
                        }
                    }
                }
            }
        }
    }
}

/** How much of a key's height the numeral on it gets. */
private const val DIGIT_OF_KEY = 0.52f
private const val DIGIT_MIN = 26f
private const val DIGIT_MAX = 52f

/** A word key against a numeral key, and the size below which the word stops being readable. */
private const val WORD_OF_DIGIT = 0.55f
private const val WORD_MIN = 14f

@Composable
private fun RowScope.Key(
    label: String,
    digitSize: TextUnit,
    wordSize: TextUnit,
    onClick: () -> Unit,
) {
    val isDigit = label.length == 1 && label[0].isDigit()
    val type = LightThemeTokens.typography
    val color = LightThemeTokens.colors.content
    Box(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .lightClickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = if (isDigit) {
                type.title.copy(fontSize = digitSize, lineHeight = digitSize, color = color)
            } else {
                type.button.copy(fontSize = wordSize, lineHeight = wordSize, color = color)
            },
            maxLines = 1,
        )
    }
}
