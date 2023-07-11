package me.ste.library.transfer.forge.reverse

import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage
import java.util.function.Consumer
import java.util.function.Supplier

class EnergyContainerItemReverseAdapter(
    handler: Supplier<IEnergyStorage?>,
    private val updateItem: Runnable
) : EnergyContainerReverseAdapter({ handler.get() }) {
    override fun accept(side: Direction?, energy: Long): Long {
        val result = super.accept(side, energy)
        this.updateItem.run()
        return result
    }

    override fun output(side: Direction?, energy: Long): Long {
        val result = super.output(side, energy)
        this.updateItem.run()
        return result
    }
}