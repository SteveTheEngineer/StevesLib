package me.ste.library.container.forge.simulation

import me.ste.library.resource.ItemResource
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import kotlin.math.absoluteValue

class SimulatedItemSlot(
    val storage: IItemHandler,
    val slot: Int,

    var resource: ItemResource = ItemResource.EMPTY,
    var change: Int = 0
) {
    private fun doChange(simulate: Boolean) =
            if (this.change > 0) {
                this.change - this.storage.insertItem(this.slot, this.resource.toStack(this.change), simulate).count
            } else if (this.change < 0) {
                -this.storage.extractItem(this.slot, -this.change, simulate).count
            } else {
                0
            }

    private fun canAcceptStack(resource: ItemResource) = this.change == 0 || this.resource.isSame(resource)

    fun exchange(amount: Int, resource: ItemResource): Int {
        if (amount > 0 && !this.canAcceptStack(resource)) {
            return 0
        }

        this.resource = resource

        val oldChange = this.change
        this.change += amount
        val newChange = this.doChange(true)
        this.change = newChange

        if (this.change == 0) {
            this.resource = ItemResource.EMPTY
        }

        return (newChange - oldChange).absoluteValue
    }

    fun keep() {
        this.doChange(false)
    }

    fun copy() = SimulatedItemSlot(this.storage, this.slot, this.resource, this.change)
}