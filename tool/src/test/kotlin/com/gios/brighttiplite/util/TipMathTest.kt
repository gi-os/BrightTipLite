package com.gios.brighttiplite.util

import kotlin.test.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TipMathTest {

    @Test
    fun formatsCentsAsDollars() {
        assertEquals("$0.00", 0L.asMoney())
        assertEquals("$0.05", 5L.asMoney())
        assertEquals("$12.34", 1234L.asMoney())
        assertEquals("$1,234.56", 123456L.asMoney())
        assertEquals("$999,999.99", 99_999_999L.asMoney())
        assertEquals("$1,000,000.00", 100_000_000L.asMoney())
    }

    @Test
    fun formatsNegativeAmounts() {
        assertEquals("-$12.34", (-1234L).asMoney())
    }

    @Test
    fun tipIsRoundedToTheNearestCent() {
        assertEquals(200L, tipCentsFor(1000L, 20))
        // 15% of $12.34 is 185.1 cents.
        assertEquals(185L, tipCentsFor(1234L, 15))
        // 18% of $0.25 is 4.5 cents, which rounds half up.
        assertEquals(5L, tipCentsFor(25L, 18))
        assertEquals(0L, tipCentsFor(0L, 22))
        assertEquals(0L, tipCentsFor(5000L, 0))
        assertEquals(5000L, tipCentsFor(5000L, 100))
    }

    @Test
    fun tipOnTheLargestBillIsExact() {
        // 20% of $999,999.99 is 19,999,999.8 cents.
        assertEquals(20_000_000L, tipCentsFor(99_999_999L, 20))
    }

    @Test
    fun tipNeverDriftsFromTheExactFigureByMoreThanHalfACent() {
        for (cents in 0L..20_000L step 7) {
            for (percent in 0..100) {
                val exactTimes100 = cents * percent
                val got = tipCentsFor(cents, percent) * 100
                assertTrue(abs(got - exactTimes100) <= 50, "$cents at $percent%")
            }
        }
    }
}
