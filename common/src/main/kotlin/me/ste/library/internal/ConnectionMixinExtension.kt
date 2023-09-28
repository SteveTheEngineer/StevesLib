package me.ste.library.internal

import me.ste.library.network.StevesLibConnection
import net.minecraft.network.ConnectionProtocol

interface ConnectionMixinExtension {
    val steveslib_connection: StevesLibConnection
    val steveslib_protocol: ConnectionProtocol
}