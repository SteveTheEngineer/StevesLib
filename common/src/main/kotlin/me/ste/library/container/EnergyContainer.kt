package me.ste.library.container

import me.ste.library.transaction.TransactionShard

interface EnergyContainer {
    /**
     * The energy stored.
     */
    val stored: Long

    /**
     * The maximum energy that can be stored.
     */
    val capacity: Long

    /**
     * Accept energy.
     *
     * @return the energy that was accepted, <= [energy].
     */
    fun accept(energy: Long, transaction: TransactionShard): Long

    /**
     * Output energy.
     *
     * @return the energy that was output, <= [energy].
     */
    fun output(energy: Long, transaction: TransactionShard): Long

    /**
     * If this returns false, any call to [accept] should return `0L`.
     * Returns true if the container can accept energy.
     */
    val canAccept: Boolean

    /**
     * If this returns false, any call to [output] should return `0L`.
     * Returns true if the container can output energy.
     */
    val canOutput: Boolean
}