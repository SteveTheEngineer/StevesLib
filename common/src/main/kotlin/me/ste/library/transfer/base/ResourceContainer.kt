package me.ste.library.transfer.base

import me.ste.library.transfer.resource.ResourceWithAmount
import net.minecraft.core.Direction

interface ResourceContainer<T> {
    fun getContainerSize(side: Direction?): Int

    fun getResource(side: Direction?, slot: Int): ResourceWithAmount<T>
    fun getCapacity(side: Direction?, slot: Int): Long

    fun canAccept(side: Direction?, slot: Int, resource: T): Boolean

    fun canAccept(side: Direction?): Boolean
    fun canOutput(side: Direction?): Boolean
}