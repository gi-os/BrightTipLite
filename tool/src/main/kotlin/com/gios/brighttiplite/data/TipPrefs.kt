package com.gios.brighttiplite.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first

/** The one thing the tool remembers between launches: the last tip percent used. */
class TipPrefs(private val dataStore: DataStore<Preferences>) {

    suspend fun lastPercent(): Int? =
        runCatching { dataStore.data.first()[KEY_PERCENT] }.getOrNull()

    suspend fun setLastPercent(percent: Int) {
        runCatching { dataStore.edit { it[KEY_PERCENT] = percent } }
    }

    private companion object {
        val KEY_PERCENT = intPreferencesKey("last_tip_percent")
    }
}
