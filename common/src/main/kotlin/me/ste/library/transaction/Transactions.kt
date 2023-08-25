package me.ste.library.transaction

import dev.architectury.injectables.annotations.ExpectPlatform
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

object Transactions {
    fun <T> open(function: Function<MutableTransactionShard, T>): T {
        var result: T? = null

        this.open {
            result = function.apply(it)
        }

        return result as T
    }

    fun <T> open(parent: TransactionShard, function: Function<MutableTransactionShard, T>): T {
        var result: T? = null

        this.open(parent) {
            result = function.apply(it)
        }

        return result as T
    }

    @JvmStatic
    @ExpectPlatform
    fun open(consumer: Consumer<MutableTransactionShard>) {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun open(parent: TransactionShard, consumer: Consumer<MutableTransactionShard>) {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun current(): TransactionShard? {
        throw UnsupportedOperationException()
    }
}