package me.ste.library.container.resource

import me.ste.library.resource.FluidResource
import me.ste.library.transaction.TransactionShard

interface ResourceContainer<R> : Iterable<ResourceHolder<R>> {
    /**
     * The number of slots in the container.
     * If the container does not use slots, this should return -1.
     */
    val slots: Int

    /**
     * For containers that use slots.
     *
     * @return the resource holder or null if the container does not have slots, or the slot index is out of range.
     */
    fun getSlot(slot: Int): ResourceHolder<R>?

    /**
     * For containers that can store large amounts of unique types of resources.
     * Should not be implemented for slot containers.
     *
     * @return the resource holder or null if the container can't provide direct access to the specific resource.
     */
    fun getResource(resource: R): ResourceHolder<R>?

    /**
     * Add the resource to the container.
     *
     * @return the amount that was added, <= [amount].
     */
    fun accept(resource: R, amount: Long, transaction: TransactionShard): Long

    /**
     * Output a certain amount of the resource from the container.
     *
     * @return the amount that was output, <= [amount].
     */
    fun output(resource: R, amount: Long, transaction: TransactionShard): Long

    /**
     * @return true; if the container can accept this specific type of [resource].
     */
    fun canAccept(resource: R): Boolean

    /**
     * @return true; if the container can output this specific type of [resource].
     */
    fun canOutput(resource: R): Boolean

    /**
     * If this returns false, any call to [accept] should return `0L`.
     * Returns true if the container can accept resources.
     */
    val canAccept: Boolean

    /**
     * If this returns false, any call to [output] should return `0L`.
     * Returns true if the container can output resources.
     */
    val canOutput: Boolean
}