package com.gios.brighttiplite.ui

import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewModelScope
import com.gios.brighttiplite.data.TipPrefs
import com.gios.brighttiplite.util.TIP_PRESETS
import com.gios.brighttiplite.util.TipState
import com.gios.brighttiplite.util.asMoney
import com.thelightphone.sdk.InitialScreen
import com.thelightphone.sdk.LightScreen
import com.thelightphone.sdk.LightViewModel
import com.thelightphone.sdk.SealedLightActivity
import com.thelightphone.sdk.ui.LightText
import com.thelightphone.sdk.ui.LightTextVariant
import com.thelightphone.sdk.ui.LightTopBar
import com.thelightphone.sdk.ui.LightTopBarCenter
import com.thelightphone.sdk.ui.gridUnitsAsDp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TipViewModel(private val prefs: TipPrefs) : LightViewModel<Unit>() {

    private val _state = MutableStateFlow(TipState())
    val state: StateFlow<TipState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val saved = prefs.lastPercent() ?: return@launch
            _state.update { it.withPercent(saved) }
        }
    }

    fun pushDigit(digit: Int) = _state.update { it.pushDigit(digit) }

    fun backspace() = _state.update { it.backspace() }

    fun clearAmount() = _state.update { it.clear() }

    fun setTipPercent(percent: Int) {
        val next = _state.value.withPercent(percent)
        _state.value = next
        viewModelScope.launch { prefs.setLastPercent(next.tipPercent) }
    }

    /** The wheel nudges the tip one point per click: turning up raises it, down lowers it. */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val dir = Wheel.direction(keyCode, event)
        if (dir == 0) return false
        setTipPercent(_state.value.tipPercent - dir)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean = Wheel.direction(keyCode, event) != 0
}

/**
 * BrightTip's Tip mode: the bill at the top, tip and total under it, the preset chips,
 * then the keypad taking whatever height is left.
 */
@InitialScreen
class TipScreen(sealedActivity: SealedLightActivity) :
    LightScreen<Unit, TipViewModel>(sealedActivity) {

    override val viewModelClass: Class<TipViewModel> get() = TipViewModel::class.java

    override fun createViewModel() = TipViewModel(TipPrefs(lightContext.dataStore))

    private fun openCustom(current: Int) {
        navigateTo({ PercentScreen(it, current) }) { percent ->
            viewModel.setTipPercent(percent)
        }
    }

    @Composable
    override fun Content() {
        val state by viewModel.state.collectAsState()

        TipSurface {
            Column(Modifier.fillMaxSize()) {
                LightTopBar(center = LightTopBarCenter.Text("Tip"))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 1f.gridUnitsAsDp(), vertical = 0.3f.gridUnitsAsDp()),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LightText(text = "BILL", variant = LightTextVariant.Fine, lighten = true)
                    Box(Modifier.weight(1f))
                    LightText(
                        text = state.amountCents.asMoney(),
                        variant = LightTextVariant.Title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Rule()
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 1f.gridUnitsAsDp(), vertical = 0.3f.gridUnitsAsDp()),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Figure("TIP", state.tipCents.asMoney(), Modifier.weight(1f), alignEnd = false)
                    Figure("TOTAL", state.totalCents.asMoney(), Modifier.weight(1f), alignEnd = true)
                }
                Rule()
                PresetGrid(
                    selected = state.tipPercent,
                    isCustom = state.isCustomPercent,
                    onSelect = viewModel::setTipPercent,
                    onCustom = { openCustom(state.tipPercent) },
                )
                Rule()
                Keypad(
                    onDigit = viewModel::pushDigit,
                    onBackspace = viewModel::backspace,
                    onClear = viewModel::clearAmount,
                )
            }
        }
    }
}

@Composable
private fun Figure(label: String, value: String, modifier: Modifier, alignEnd: Boolean) {
    Column(
        modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        LightText(text = label, variant = LightTextVariant.Fine, lighten = true)
        LightText(
            text = value,
            variant = LightTextVariant.Heading,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Five presets and OTHER, three to a row. A custom percent shows on the OTHER chip. */
@Composable
private fun PresetGrid(
    selected: Int,
    isCustom: Boolean,
    onSelect: (Int) -> Unit,
    onCustom: () -> Unit,
) {
    val rows = TIP_PRESETS.chunked(3)
    Column(Modifier.fillMaxWidth()) {
        rows.forEachIndexed { rowIndex, row ->
            Row(Modifier.fillMaxWidth().height(CHIP_HEIGHT_UNITS.gridUnitsAsDp())) {
                row.forEach { percent ->
                    Chip(
                        label = "$percent%",
                        selected = !isCustom && percent == selected,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelect(percent) },
                    )
                }
                repeat(3 - row.size) {
                    Chip(
                        label = if (isCustom) "$selected%" else "OTHER",
                        selected = isCustom,
                        modifier = Modifier.weight(1f),
                        onClick = onCustom,
                    )
                }
            }
            if (rowIndex != rows.lastIndex) Rule()
        }
    }
}

private const val CHIP_HEIGHT_UNITS = 1.6f
