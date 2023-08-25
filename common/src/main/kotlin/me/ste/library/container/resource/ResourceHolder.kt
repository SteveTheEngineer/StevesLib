package me.ste.library.container.resource

import me.ste.library.transaction.TransactionShard

interface ResourceHolder<R> {
    /**
     * The type of the resource.
     */
    val resource: R

    /**
     * The amount of the resource.
     */
    val amount: Long

    /**
     * The maximum amount of the resource in the slot,
     * or otherwise the maximum amount that can be stored by the container
     * if the container is not a slot container.
     *
     * If the value is -1, the capacity is undefined.
     */
    val capacity: Long

    /**
     * true, if the resource is empty.
     */
    val isEmpty: Boolean

    /**
     * The slot represented by this resource holder.
     *
     * If the value is -1, the container does not have slots.
     */
    val slot: Int

    /**
     * Add the specified amount of the resource.
     *
     * @return the amount of the resource that was added, <= [amount].
     */
    fun accept(resource: R, amount: Long, transaction: TransactionShard): Long

    /**
     * Output the specified amount of the resource.
     *
     * @return the amount of the resource that was output, <= [amount].
     */
    fun output(amount: Long, transaction: TransactionShard): Long

    /**
     * @return true; if this resource holder can accept the specified resource type.
     */
    fun canAccept(resource: R): Boolean

    /**
     * @return true; if this resource holder can output the specified resource type.
     */
    fun canOutput(resource: R): Boolean

    /**
     * If this returns false, any call to [accept] should return `0L`.
     * Returns true if this holder can accept resources.
     */
    val canAccept: Boolean

    /**
     * If this returns false, any call to [output] should return `0L`.
     * Returns true if this holder can output resources.
     */
    val canOutput: Boolean

    fun trySetResource(resource: R, amount: Long, transaction: TransactionShard) =
            transaction.openTransaction<Boolean> {
                val toOutput = this.amount

                if (this.output(toOutput, it) != toOutput) {
                    return@openTransaction false
                }

                if (this.accept(resource, amount, it) != amount) {
                    return@openTransaction false
                }

                it.keep()
                return@openTransaction true
            }
}