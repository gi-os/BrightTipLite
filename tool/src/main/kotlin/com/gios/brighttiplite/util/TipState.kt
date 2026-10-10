package com.gios.brighttiplite.util

val TIP_PRESETS = listOf(10, 15, 18, 20, 22)
const val DEFAULT_TIP_PERCENT = 20
const val MAX_TIP_PERCENT = 100

/** The largest bill the readout has room for: $999,999.99. */
const val MAX_BILL_CENTS = 99_999_999L

/**
 * The whole tip screen as one value. Money is cents in a Long, never a Double, the same
 * as BrightTip: a Double cannot hold most cent amounts exactly.
 */
data class TipState(
    val amountCents: Long = 0L,
    val tipPercent: Int = DEFAULT_TIP_PERCENT,
) {
    val tipCents: Long get() = tipCentsFor(amountCents, tipPercent)
    val totalCents: Long get() = amountCents + tipCents

    /** A percent that is not one of the preset chips shows on the OTHER chip instead. */
    val isCustomPercent: Boolean get() = tipPercent !in TIP_PRESETS

    /** Digits push in from the right, capped so the readout cannot overflow its row. */
    fun pushDigit(digit: Int): TipState {
        val next = amountCents * 10 + digit
        return if (next > MAX_BILL_CENTS) this else copy(amountCents = next)
    }

    fun backspace(): TipState = copy(amountCents = amountCents / 10)

    fun clear(): TipState = copy(amountCents = 0L)

    fun withPercent(percent: Int): TipState = copy(tipPercent = percent.coerceIn(0, MAX_TIP_PERCENT))
}

/**
 * Typing a percent on the keypad. Same rule as BrightTip's custom tip dialog: a digit
 * that would push the value past 100 is ignored.
 */
object PercentEntry {
    fun push(percent: Int, digit: Int): Int {
        val next = percent * 10 + digit
        return if (next <= MAX_TIP_PERCENT) next else percent
    }

    fun backspace(percent: Int): Int = percent / 10
}
