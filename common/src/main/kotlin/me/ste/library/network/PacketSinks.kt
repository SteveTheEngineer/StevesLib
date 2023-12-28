package me.ste.library.network

import dev.architectury.networking.transformers.PacketSink
import dev.architectury.utils.Env
import dev.architectury.utils.GameInstance
import me.ste.library.internal.ConnectionMixinExtension
import me.ste.library.network.sink.ExtendedPacketSink
import net.minecraft.client.Minecraft
import net.minecraft.network.Connection
import net.minecraft.network.ConnectionProtocol
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk

object PacketSinks {
    private val DUMMY = PacketSink {}
    private val SERVER = PacketSink.client()

    fun dummy(env: Env = Env.SERVER, protocol: ConnectionProtocol = ConnectionProtocol.PLAY): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(env, protocol, DUMMY)

    fun server(protocol: ConnectionProtocol = ConnectionProtocol.PLAY): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.CLIENT, protocol, SERVER)

    fun connection(connection: Connection): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper.from(StevesLibConnection.get(connection)) { connection.send(it) }

    fun connection(connection: StevesLibConnection): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper.from(connection) { connection.vanillaConnection.send(it) }

    fun player(player: ServerPlayer): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.SERVER, ConnectionProtocol.PLAY, PacketSink.ofPlayer(player))

    fun players(players: Iterable<ServerPlayer>): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.SERVER, ConnectionProtocol.PLAY, PacketSink.ofPlayers(players))

    fun dimension(dimension: ResourceKey<Level>): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.SERVER, ConnectionProtocol.PLAY) {
            getServer().playerList.broadcastAll(it, dimension)
        }

    fun allPlayers(): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.SERVER, ConnectionProtocol.PLAY) {
            getServer().playerList.broadcastAll(it)
        }

    fun playersNear(except: ServerPlayer?, x: Double, y: Double, z: Double, radius: Double, dimension: ResourceKey<Level>): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.SERVER, ConnectionProtocol.PLAY) {
            getServer().playerList.broadcast(except, x, y, z, radius, dimension, it)
        }

    fun trackingEntity(entity: Entity): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.SERVER, ConnectionProtocol.PLAY) {
            (entity.level().chunkSource as ServerChunkCache).broadcast(entity, it)
        }

    fun trackingEntityAndSelf(entity: Entity): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.SERVER, ConnectionProtocol.PLAY) {
            (entity.level().chunkSource as ServerChunkCache).broadcastAndSend(entity, it)
        }

    fun trackingChunk(chunk: LevelChunk): ExtendedPacketSink =
        ExtendedPacketSink.Wrapper(Env.SERVER, ConnectionProtocol.PLAY) {
            val players = (chunk.level.chunkSource as ServerChunkCache).chunkMap.getPlayers(chunk.pos, false)

            for (player in players) {
                player.connection.send(it)
            }
        }

    private fun getServer() =
        GameInstance.getServer() ?: throw IllegalStateException("Unable to send packet when the server is not running.")
}