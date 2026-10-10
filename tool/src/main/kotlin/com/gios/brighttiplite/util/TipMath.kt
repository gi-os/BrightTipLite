package com.gios.brighttiplite.util

import kotlin.math.abs
import kotlin.math.roundToLong

// Ported from BrightTip's util/TipMath.kt. Only the two functions the tip calculator uses
// came over; the split and allocation code stayed behind with the split feature.

/** "$1,234.56". Everything in this tool is cents, so this is the only place decimals appear. */
fun Long.asMoney(): String {
    val negative = this < 0
    val cents = abs(this)
    val whole = cents / 100
    val frac = cents % 100
    val grouped = whole.toString().reversed().chunked(3).joinToString(",").reversed()
    return (if (negative) "-$" else "$") + grouped + "." + frac.toString().padStart(2, '0')
}

/** Tip is taken on the pre-tax amount, the usual US convention. */
fun tipCentsFor(baseCents: Long, percent: Int): Long =
    (baseCents * percent / 100.0).roundToLong()
