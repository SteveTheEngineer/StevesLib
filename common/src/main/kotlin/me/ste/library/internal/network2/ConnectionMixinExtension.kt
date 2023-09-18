package me.ste.library.internal.network2

import net.minecraft.network.ConnectionProtocol

interface ConnectionMixinExtension {
    val steveslib_connection: StevesLibConnection
    val steveslib_protocol: ConnectionProtocol
}