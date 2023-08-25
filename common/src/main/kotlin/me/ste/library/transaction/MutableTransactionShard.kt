package me.ste.library.transaction

import java.util.function.Consumer

interface MutableTransactionShard : TransactionShard {
    fun keep()
}