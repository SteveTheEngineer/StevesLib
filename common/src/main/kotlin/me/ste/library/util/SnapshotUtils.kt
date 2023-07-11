package me.ste.library.util

import me.ste.library.transfer.base.SnapshotProvider
import net.minecraft.core.Direction
import java.util.function.Function

object SnapshotUtils {
    fun readSnapshot(provider: SnapshotProvider<*>, side: Direction?, snapshot: Any?) {
        (provider as SnapshotProvider<Any?>).readSnapshot(side, snapshot)
    }

    fun <T : SnapshotProvider<*>, R> simulate(provider: T, side: Direction?, doSimulate: Boolean, consumer: Function<T, R>): R {
        val snapshot = provider.createSnapshot(side)

        val result = consumer.apply(provider)

        if (doSimulate) {
            readSnapshot(provider, side, snapshot)
        } else {
            provider.saveChanges(side)
        }

        return result
    }
}