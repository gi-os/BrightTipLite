package com.gios.brighttiplite.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TipStateTest {

    private fun typed(vararg digits: Int): TipState =
        digits.fold(TipState()) { state, d -> state.pushDigit(d) }

    @Test
    fun digitsPushInFromTheRight() {
        assertEquals(1234L, typed(1, 2, 3, 4).amountCents)
        assertEquals(5L, typed(0, 0, 5).amountCents)
    }

    @Test
    fun billIsCappedAtTheReadoutWidth() {
        val full = typed(9, 9, 9, 9, 9, 9, 9, 9)
        assertEquals(MAX_BILL_CENTS, full.amountCents)
        assertEquals(MAX_BILL_CENTS, full.pushDigit(9).amountCents)
    }

    @Test
    fun backspaceAndClear() {
        assertEquals(123L, typed(1, 2, 3, 4).backspace().amountCents)
        assertEquals(0L, typed(1).backspace().amountCents)
        assertEquals(0L, typed(1, 2, 3).clear().amountCents)
    }

    @Test
    fun clearKeepsThePercent() {
        assertEquals(15, typed(1, 2).withPercent(15).clear().tipPercent)
    }

    @Test
    fun tipAndTotal() {
        val state = typed(4, 5, 0, 0).withPercent(18)
        assertEquals(810L, state.tipCents)
        assertEquals(5310L, state.totalCents)
    }

    @Test
    fun defaultsToTwentyPercent() {
        assertEquals(DEFAULT_TIP_PERCENT, TipState().tipPercent)
        assertFalse(TipState().isCustomPercent)
    }

    @Test
    fun percentIsClamped() {
        assertEquals(0, TipState().withPercent(-1).tipPercent)
        assertEquals(MAX_TIP_PERCENT, TipState().withPercent(250).tipPercent)
    }

    @Test
    fun customPercentIsAnythingOffThePresets() {
        TIP_PRESETS.forEach { assertFalse(TipState().withPercent(it).isCustomPercent) }
        assertTrue(TipState().withPercent(17).isCustomPercent)
        assertTrue(TipState().withPercent(0).isCustomPercent)
    }

    @Test
    fun percentEntryIgnoresDigitsPastOneHundred() {
        assertEquals(1, PercentEntry.push(0, 1))
        assertEquals(12, PercentEntry.push(1, 2))
        assertEquals(12, PercentEntry.push(12, 5))
        assertEquals(100, PercentEntry.push(10, 0))
        assertEquals(10, PercentEntry.push(10, 1))
        assertEquals(100, PercentEntry.push(100, 0))
        assertEquals(1, PercentEntry.backspace(12))
        assertEquals(0, PercentEntry.backspace(0))
    }
}
