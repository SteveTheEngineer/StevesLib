package me.ste.library.transaction.fabric

import me.ste.library.transaction.MutableTransactionShard
import me.ste.library.transaction.TransactionResult
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import java.util.function.Consumer

class MutableTransactionWrapper(
    val mutableTransaction: Transaction
) : TransactionContextWrapper(mutableTransaction), MutableTransactionShard {
    override fun keep() {
        this.mutableTransaction.commit()
    }
}