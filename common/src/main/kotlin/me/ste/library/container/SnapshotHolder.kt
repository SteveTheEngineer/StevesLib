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
        if (transaction.depth in this.snapshots) {
            return
        }

        this.snapshots[transaction.depth] = this.snapshotSupplier.get()

        transaction.onEnd {
            this.onTransactionEnd(transaction, it)
        }
    }

    private fun onTransactionEnd(transaction: TransactionShard, result: TransactionResult) {
        val snapshot = this.snapshots.remove(transaction.depth)
            ?: return

        if (result == TransactionResult.REVERT) {
            this.snapshotConsumer.accept(snapshot)
            return
        }

        val parent = transaction.parent

        if (parent == null) {
            transaction.onFinalEnd {
                this.onKeep.run()
            }
            return
        }

        if (parent.depth in this.snapshots) {
            return
        }

        this.snapshots[parent.depth] = snapshot
        parent.onEnd {
            this.onTransactionEnd(parent, it)
        }
    }
}