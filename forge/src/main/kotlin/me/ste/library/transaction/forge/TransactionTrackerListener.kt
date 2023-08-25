package me.ste.library.transaction.forge

import me.ste.library.transaction.TransactionResult
import me.ste.library.transaction.TransactionShard

interface TransactionTrackerListener {
    fun onStart(transaction: TransactionShard)
    fun onEnd(transaction: TransactionShard, result: TransactionResult)
}