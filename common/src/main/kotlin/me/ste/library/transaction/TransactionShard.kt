package me.ste.library.transaction

import java.util.function.Consumer
import java.util.function.Function

interface TransactionShard {
    val parent: TransactionShard?
    val depth: Int

    fun openTransaction(consumer: Consumer<MutableTransactionShard>){
        Transactions.open(this, consumer)
    }
    fun <T> openTransaction(function: Function<MutableTransactionShard, T>): T {
        return Transactions.open(this, function)
    }

    fun onEnd(callback: Consumer<TransactionResult>)
    fun onFinalEnd(callback: Consumer<TransactionResult>)
}