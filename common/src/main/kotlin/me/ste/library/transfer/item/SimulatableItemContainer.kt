package me.ste.library.transfer.item

import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableItem
import net.minecraft.core.Direction

interface SimulatableItemContainer : ItemContainer {
    fun simulateAccept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>): Long // returns accepted amount
    fun simulateOutput(side: Direction?, slot: Int, amount: Long): Long // returns output amount
}