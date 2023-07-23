package com.vitorpamplona.amethyst.model

import androidx.compose.runtime.Stable
import com.vitorpamplona.amethyst.R

@Stable
class Settings(
    var preferredLanguage: String? = null,
    var automaticallyShowImages: ConnectivityType = ConnectivityType.ALWAYS,
    var automaticallyStartPlayback: ConnectivityType = ConnectivityType.ALWAYS,
    var automaticallyShowUrlPreview: ConnectivityType = ConnectivityType.ALWAYS
)

enum class ConnectivityType(val prefCode: Boolean?, val screenCode: Int, val reourceId: Int) {
    ALWAYS(null, 0, R.string.connectivity_type_always),
    WIFI_ONLY(true, 1, R.string.connectivity_type_wifi_only),
    NEVER(false, 2, R.string.connectivity_type_never)
}

fun parseConnectivityType(code: Boolean?): ConnectivityType {
    return when (code) {
        ConnectivityType.ALWAYS.prefCode -> ConnectivityType.ALWAYS
        ConnectivityType.WIFI_ONLY.prefCode -> ConnectivityType.WIFI_ONLY
        ConnectivityType.NEVER.prefCode -> ConnectivityType.NEVER
        else -> {
            ConnectivityType.ALWAYS
        }
    }
}

fun parseConnectivityType(screenCode: Int): ConnectivityType {
    return when (screenCode) {
        ConnectivityType.ALWAYS.screenCode -> ConnectivityType.ALWAYS
        ConnectivityType.WIFI_ONLY.screenCode -> ConnectivityType.WIFI_ONLY
        ConnectivityType.NEVER.screenCode -> ConnectivityType.NEVER
        else -> {
            ConnectivityType.ALWAYS
        }
    }
}
