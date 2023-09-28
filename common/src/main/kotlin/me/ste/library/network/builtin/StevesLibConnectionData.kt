package me.ste.library.network.builtin

import me.ste.library.network.data.ConnectionDataKey

class StevesLibConnectionData {
    companion object {
        val KEY = ConnectionDataKey<StevesLibConnectionData>()
    }

    var customDisconnect = false
}