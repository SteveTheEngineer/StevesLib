package me.ste.library.transfer.forge.adapter

import me.ste.library.transfer.energy.EnergyContainerItem
import net.minecraft.world.item.ItemStack

class EnergyContainerForgeItemAdapter(
    private val stack: ItemStack,
    private val itemContainer: EnergyContainerItem<*>
) : EnergyContainerForgeAdapter(itemContainer, null) {
    override fun receiveEnergy(energy: Int, simulate: Boolean): Int {
        val result = super.receiveEnergy(energy, simulate)
        this.itemContainer.applyResult(this.stack)
        return result
    }

    override fun extractEnergy(energy: Int, simulate: Boolean): Int {
        val result = super.extractEnergy(energy, simulate)
        this.itemContainer.applyResult(this.stack)
        return result
    }
}