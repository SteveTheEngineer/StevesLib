package me.ste.library.network

import dev.architectury.utils.GameInstance
import net.minecraft.client.Minecraft
import net.minecraft.network.Connection
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import java.util.function.BiConsumer
import kotlin.math.pow

object NetworkMessageDistribution {
    fun <T> server(consumer: BiConsumer<Connection, T>, message: T) {
        val connection = Minecraft.getInstance().connection!!.connection
        consumer.accept(connection, message)
    }

    fun <T> player(consumer: BiConsumer<Connection, T>, message: T, player: ServerPlayer) {
        consumer.accept(player.connection.connection, message)
    }

    fun <T> players(consumer: BiConsumer<Connection, T>, message: T, players: Iterable<ServerPlayer>) {
        for (player in players) {
            consumer.accept(player.connection.connection, message)
        }
    }

    fun <T> allPlayers(consumer: BiConsumer<Connection, T>, message: T) {
        val server = GameInstance.getServer()!!

        for (player in server.playerList.players) {
            consumer.accept(player.connection.connection, message)
        }
    }

    fun <T> allInLevel(consumer: BiConsumer<Connection, T>, message: T, level: ResourceKey<Level>) {
        val server = GameInstance.getServer()!!

        for (player in server.playerList.players) {
            if (player.level().dimension() != level) {
                continue
            }

            consumer.accept(player.connection.connection, message)
        }
    }

    fun <T> aroundPos(consumer: BiConsumer<Connection, T>, message: T, except: Player?, x: Double, y: Double, z: Double, radius: Double, level: ResourceKey<Level>) {
        val server = GameInstance.getServer()!!

        for (player in server.playerList.players) {
            if (player == except) {
                continue
            }

            if (player.level().dimension() != level) {
                continue
            }

            val distanceX = (player.x - x).pow(2)
            val distanceY = (player.y - y).pow(2)
            val distanceZ = (player.z - z).pow(2)

            if (distanceX + distanceY + distanceZ >= radius.pow(2)) {
                continue
            }

            consumer.accept(player.connection.connection, message)
        }
    }

    fun <T> trackingEntity(consumer: BiConsumer<Connection, T>, message: T, entity: Entity) {
        val chunkMap = (entity.level() as ServerLevel).chunkSource.chunkMap
        val entityMap = chunkMap.entityMap
        val trackedEntity = entityMap.get(entity.id) ?: return

        for (serverConnection in trackedEntity.seenBy) {
            val player = serverConnection.player
            consumer.accept(player.connection.connection, message)
        }
    }

    fun <T> trackingEntityAndSelf(consumer: BiConsumer<Connection, T>, message: T, entity: Entity) {
        this.trackingEntity(consumer, message, entity)

        if (entity is ServerPlayer) {
            consumer.accept(entity.connection.connection, message)
        }
    }

    fun <T> trackingChunk(consumer: BiConsumer<Connection, T>, message: T, chunk: LevelChunk) {
        val chunkMap = (chunk.level.chunkSource as ServerChunkCache).chunkMap
        val players = chunkMap.getPlayers(chunk.pos, false)

        for (player in players) {
            consumer.accept(player.connection.connection, message)
        }
    }
}