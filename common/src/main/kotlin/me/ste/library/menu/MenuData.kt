package me.ste.library.menu

import io.netty.buffer.Unpooled
import me.ste.library.network.NetworkMessageDistribution
import me.ste.library.network.builtin.MenuDataS2C
import me.ste.library.network.builtin.StevesLibBuiltinChannel
//import me.ste.library.internal.network.MenuDataMessageS2C
//import me.ste.library.internal.network.StevesLibNetwork
//import me.ste.library.network.PacketSinks
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object MenuData {
    fun sync(menu: MenuDataProvider, player: Player) {
        if (player !is ServerPlayer) {
            return
        }

        val map = mutableMapOf<Int, ByteArray>()

        for ((index, entry) in menu.data.withIndex()) {
            if (!entry.needsSync) {
                continue
            }

            val buf = FriendlyByteBuf(Unpooled.buffer())
            entry.write(buf)
            map[index] = buf.array()

            entry.markSynced()
        }

        if (map.isEmpty()) {
            return
        }

        NetworkMessageDistribution.player(StevesLibBuiltinChannel::sendMessage, MenuDataS2C(menu.containerId, map), player)
    }
}