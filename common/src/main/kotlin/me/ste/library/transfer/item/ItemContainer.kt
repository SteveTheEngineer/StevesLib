package me.ste.library.transfer.item

import me.ste.library.transfer.base.ResourceContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableItem
import net.minecraft.core.Direction

interface ItemContainer : ResourceContainer<StackableItem> {
    fun accept(side: Direction?, slot: Int, resource: ResourceWithAmount<StackableItem>): Long // returns accepted amount
    fun output(side: Direction?, slot: Int, amount: Long): Long // returns output amount
}