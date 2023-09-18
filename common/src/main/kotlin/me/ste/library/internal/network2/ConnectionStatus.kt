package me.ste.library.internal.network2

enum class ConnectionStatus {
    NONE,

    NEGOTIATING_COMPATIBILITY,
    NEGOTIATING_RESERVATION,

    ERROR,
    UNSUPPORTED,
    INCOMPATIBLE,

    READY
}