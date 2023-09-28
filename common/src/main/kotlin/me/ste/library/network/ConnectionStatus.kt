package me.ste.library.network

enum class ConnectionStatus(
    val isFinal: Boolean = false
) {
    NONE,

    NEGOTIATING,

    UNSUPPORTED(true),
    INCOMPATIBLE(true),

    READY(true)
}