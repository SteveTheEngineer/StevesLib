package me.ste.library.transaction.forge

import me.ste.library.transaction.MutableTransactionShard
import me.ste.library.transaction.TransactionResult
import java.util.function.Consumer

class ForgeTransaction(
    val parent: ForgeTransaction? = null,
    override val depth: Int = 0
) : MutableTransactionShard {
    val onEndCallbacks = mutableListOf<Consumer<TransactionResult>>()
    val onFinalEndCallbacks = mutableListOf<Consumer<TransactionResult>>()

    var result = TransactionResult.REVERT

    override fun keep() {
        this.result = TransactionResult.KEEP
    }

    override fun onEnd(callback: Consumer<TransactionResult>) {
        this.onEndCallbacks.add(0, callback)
    }

    override fun onFinalEnd(callback: Consumer<TransactionResult>) {
        this.onFinalEndCallbacks += callback
    }

    fun end() {
        for (callback in this.onEndCallbacks) {
            callback.accept(this.result)
        }

        if (this.parent != null) {
            for (callback in this.onFinalEndCallbacks) {
                this.parent.onFinalEndCallbacks.add(0, callback)
            }
        } else {
            for (callback in this.onFinalEndCallbacks) {
                callback.accept(this.result)
            }
        }
    }
}