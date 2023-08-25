package me.ste.library.container

import me.ste.library.transaction.TransactionResult
import me.ste.library.transaction.TransactionShard
import java.util.function.Consumer
import java.util.function.Supplier

class SnapshotHolder<T>(
    private val snapshotSupplier: Supplier<T>,
    private val snapshotConsumer: Consumer<T>,
    private val onKeep: Runnable
) {
    private val snapshots = mutableMapOf<Int, T>()

    fun track(transaction: TransactionShard) {
        if (this.snapshots.isEmpty()) {
            transaction.onFinalEnd {
                if (it != TransactionResult.KEEP) {
                    return@onFinalEnd
                }

                this.onKeep.run()
            }
        }

        this.snapshots[transaction.depth] = this.snapshotSupplier.get()

        transaction.onEnd {
            if (it == TransactionResult.REVERT) {
                this.snapshotConsumer.accept(this.snapshots[transaction.depth]!!)
            }

            this.snapshots -= transaction.depth
        }
    }
}