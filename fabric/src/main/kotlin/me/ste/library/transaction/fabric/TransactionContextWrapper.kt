package me.ste.library.transaction.fabric

import me.ste.library.transaction.MutableTransactionShard
import me.ste.library.transaction.TransactionResult
import me.ste.library.transaction.TransactionShard
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import java.util.function.Consumer

open class TransactionContextWrapper(
    val transaction: TransactionContext
) : TransactionShard {
    override val depth get() = this.transaction.nestingDepth()

    override fun onEnd(callback: Consumer<TransactionResult>) {
        this.transaction.addCloseCallback { transaction, result ->
            when (result) {
                TransactionContext.Result.COMMITTED -> callback.accept(TransactionResult.KEEP)
                TransactionContext.Result.ABORTED -> callback.accept(TransactionResult.REVERT)
            }
        }
    }

    override fun onFinalEnd(callback: Consumer<TransactionResult>) {
        this.transaction.addOuterCloseCallback { result ->
            when (result) {
                TransactionContext.Result.COMMITTED -> callback.accept(TransactionResult.KEEP)
                TransactionContext.Result.ABORTED -> callback.accept(TransactionResult.REVERT)
            }
        }
    }
}