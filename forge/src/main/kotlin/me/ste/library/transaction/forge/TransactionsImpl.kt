package me.ste.library.transaction.forge

import me.ste.library.transaction.MutableTransactionShard
import me.ste.library.transaction.TransactionShard
import java.util.function.Consumer

object TransactionsImpl {
    private val current = ThreadLocal.withInitial<ForgeTransaction?> { null }

    @JvmStatic
    fun open(consumer: Consumer<MutableTransactionShard>) {
        if (current.get() != null) {
            throw IllegalStateException("There is already an active transaction.")
        }

        val transaction = ForgeTransaction()

        this.current.set(transaction)
        consumer.accept(transaction)
        transaction.end()

        this.current.set(null)
    }

    @JvmStatic
    fun open(parent: TransactionShard, consumer: Consumer<MutableTransactionShard>) {
        val current = current()

        if (current != parent) {
            throw IllegalStateException("Broken transaction hierarchy.")
        }

        val transaction = ForgeTransaction(current, current.depth + 1)

        this.current.set(transaction)
        consumer.accept(transaction)
        transaction.end()

        this.current.set(current)
    }

    @JvmStatic
    fun current() = current.get() ?: throw IllegalStateException("No active transaction!")
}