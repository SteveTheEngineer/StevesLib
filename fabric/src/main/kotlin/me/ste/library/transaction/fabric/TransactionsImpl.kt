package me.ste.library.transaction.fabric

import me.ste.library.transaction.MutableTransactionShard
import me.ste.library.transaction.TransactionShard
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import java.util.function.Consumer

object TransactionsImpl {
    @JvmStatic
    fun open(consumer: Consumer<MutableTransactionShard>) {
        Transaction.openOuter().use {
            consumer.accept(
                MutableTransactionWrapper(it)
            )
        }
    }

    @JvmStatic
    fun open(parent: TransactionShard, consumer: Consumer<MutableTransactionShard>) {
        val parentWrapper = parent as? TransactionContextWrapper
            ?: throw IllegalStateException("Attempted to open a transaction with an incompatible parent.")

        Transaction.openNested(parentWrapper.transaction).use {
            consumer.accept(
                MutableTransactionWrapper(it)
            )
        }
    }

    @JvmStatic
    fun current(): TransactionShard? {
        val transaction = Transaction.getCurrentUnsafe() ?: return null
        return TransactionContextWrapper(transaction)
    }

    fun getContext(shard: TransactionShard): TransactionContext {
        val wrapper = shard as? TransactionContextWrapper
            ?: throw IllegalStateException("Attempted to get the context of an incompatible shard.")

        return wrapper.transaction
    }
}