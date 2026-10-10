package com.gios.brighttiplite.ui

import android.view.KeyEvent

/**
 * The Light Phone III's scroll wheel. LightOS labels its turns `WHEEL_CCW` (up) and
 * `WHEEL_CW` (down); when those labels are not in the key table the raw scancodes from the
 * optical sensor (19 and 20) are used, gated on the sensor's device name so a Bluetooth
 * keyboard's R and T keys are not mistaken for it. Mapping taken from BrightRecorder.
 */
object Wheel {
    private const val SENSOR = "Pixart pat9126ja"
    private const val SCAN_UP = 19
    private const val SCAN_DOWN = 20

    private val upCode: Int = KeyEvent.keyCodeFromString("WHEEL_CCW")
    private val downCode: Int = KeyEvent.keyCodeFromString("WHEEL_CW")

    /** -1 for a turn up, +1 for a turn down, 0 for any other key. */
    fun direction(keyCode: Int, event: KeyEvent): Int {
        if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
            if (keyCode == upCode) return -1
            if (keyCode == downCode) return 1
        }
        if (event.device?.name == SENSOR) {
            if (event.scanCode == SCAN_UP) return -1
            if (event.scanCode == SCAN_DOWN) return 1
        }
        return 0
    }
}
