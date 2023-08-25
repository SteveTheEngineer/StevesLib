package me.ste.library.network

import dev.architectury.networking.NetworkManager
import dev.architectury.networking.transformers.PacketSink
import dev.architectury.utils.GameInstance
import net.minecraft.network.Connection
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk

object PacketSinks {
    fun server() = PacketSinkWithSide(NetworkManager.Side.C2S, PacketSink.client())

    fun player(player: ServerPlayer) = PacketSinkWithSide(NetworkManager.Side.S2C, PacketSink.ofPlayer(player))
    fun players(players: Iterable<ServerPlayer>) = PacketSinkWithSide(NetworkManager.Side.S2C, PacketSink.ofPlayers(players))
    fun allPlayers() = PacketSinkWithSide(NetworkManager.Side.S2C) {
        GameInstance.getServer()!!.playerList.broadcastAll(it)
    }

    fun connection(connection: Connection) = PacketSinkWithSide(NetworkManager.Side.S2C, connection::send)
    fun connections(connections: Iterable<Connection>) = PacketSinkWithSide(NetworkManager.Side.S2C) {
        for (connection in connections) {
            connection.send(it)
        }
    }

    fun level(level: ResourceKey<Level>) = PacketSinkWithSide(NetworkManager.Side.S2C) {
        GameInstance.getServer()!!.playerList.broadcastAll(it, level)
    }
    fun aroundPos(except: Player?, x: Double, y: Double, z: Double, radius: Double, level: ResourceKey<Level>) = PacketSinkWithSide(NetworkManager.Side.S2C) {
        GameInstance.getServer()!!.playerList.broadcast(except, x, y, z, radius, level, it)
    }
    fun trackingEntity(entity: Entity) = PacketSinkWithSide(NetworkManager.Side.S2C) {
        (entity.level as ServerLevel).chunkSource.broadcast(entity, it)
    }
    fun trackingEntityAndSelf(entity: Entity) = PacketSinkWithSide(NetworkManager.Side.S2C) {
        (entity.level as ServerLevel).chunkSource.broadcastAndSend(entity, it)
    }
    fun trackingChunk(chunk: LevelChunk) = PacketSinkWithSide(NetworkManager.Side.S2C) {
        (chunk.level.chunkSource as ServerChunkCache).chunkMap.getPlayers(chunk.pos, false)
    }
}