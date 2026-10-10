package com.gios.brighttiplite.ui

import android.view.KeyEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gios.brighttiplite.util.MAX_TIP_PERCENT
import com.gios.brighttiplite.util.PercentEntry
import com.thelightphone.sdk.SealedLightActivity
import com.thelightphone.sdk.SimpleLightScreen
import com.thelightphone.sdk.ui.LightBarButton
import com.thelightphone.sdk.ui.LightBottomBar
import com.thelightphone.sdk.ui.LightIcons
import com.thelightphone.sdk.ui.LightText
import com.thelightphone.sdk.ui.LightTextVariant
import com.thelightphone.sdk.ui.LightTopBar
import com.thelightphone.sdk.ui.LightTopBarCenter
import com.thelightphone.sdk.ui.gridUnitsAsDp

/**
 * Free entry of a tip percent on the same keypad, BrightTip's custom tip dialog as its
 * own page. USE returns the percent; back or CANCEL returns nothing. The wheel nudges
 * the figure here too.
 */
class PercentScreen(
    sealedActivity: SealedLightActivity,
    initial: Int,
) : SimpleLightScreen<Int>(sealedActivity) {

    private val percentState = mutableIntStateOf(initial.coerceIn(0, MAX_TIP_PERCENT))
    private var done = false

    private fun finish(value: Int?) {
        if (done) return
        done = true
        goBack(value)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val dir = Wheel.direction(keyCode, event)
        if (dir == 0) return false
        percentState.intValue = (percentState.intValue - dir).coerceIn(0, MAX_TIP_PERCENT)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean = Wheel.direction(keyCode, event) != 0

    @Composable
    override fun Content() {
        val percent by percentState
        TipSurface {
            Column(Modifier.fillMaxSize()) {
                LightTopBar(
                    leftButton = LightBarButton.LightIcon(LightIcons.BACK, onClick = { finish(null) }),
                    center = LightTopBarCenter.Text("Custom tip"),
                )
                Box(
                    Modifier.fillMaxWidth().padding(vertical = 0.4f.gridUnitsAsDp()),
                    contentAlignment = Alignment.Center,
                ) {
                    LightText(text = "$percent%", variant = LightTextVariant.Title)
                }
                Rule()
                Keypad(
                    onDigit = { percentState.intValue = PercentEntry.push(percentState.intValue, it) },
                    onBackspace = { percentState.intValue = PercentEntry.backspace(percentState.intValue) },
                    onClear = { percentState.intValue = 0 },
                )
                LightBottomBar(
                    listOf(
                        LightBarButton.Text("CANCEL", onClick = { finish(null) }),
                        LightBarButton.Text("USE", onClick = { finish(percentState.intValue) }),
                    ),
                )
            }
        }
    }
}
