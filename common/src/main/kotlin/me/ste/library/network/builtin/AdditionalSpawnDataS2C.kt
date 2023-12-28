package me.ste.library.network.builtin

import dev.architectury.extensions.network.EntitySpawnExtension
import io.netty.buffer.Unpooled
import me.ste.library.client.network.ClientNetworkMessage
import me.ste.library.network.channel.NetworkChannelConnection
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf

class AdditionalSpawnDataS2C : ClientNetworkMessage {
    private val entityId: Int
    private val data: ByteArray

    constructor(entityId: Int, entity: EntitySpawnExtension) {
        this.entityId = entityId

        val buf = FriendlyByteBuf(Unpooled.buffer())
        entity.saveAdditionalSpawnData(buf)
        this.data = ByteArray(buf.readableBytes())
        buf.getBytes(0, this.data)
    }

    constructor(buf: FriendlyByteBuf) {
        this.entityId = buf.readVarInt()
        this.data = buf.readByteArray()
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeVarInt(this.entityId)
        buf.writeByteArray(this.data)
    }

    override fun handle(connection: NetworkChannelConnection, minecraft: Minecraft) {
        val level = minecraft.level
            ?: return

        val entity = level.getEntity(this.entityId)
            ?: return

        if (entity !is EntitySpawnExtension) {
            return
        }

        entity.loadAdditionalSpawnData(
            FriendlyByteBuf(
                Unpooled.wrappedBuffer(this.data)
            )
        )
    }
}