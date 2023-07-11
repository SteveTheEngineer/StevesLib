package me.ste.library.transfer.provider

import me.ste.library.transfer.energy.SnapshotEnergyContainer
import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.item.SnapshotItemContainer

interface ContainerBlockEntity {
    val itemContainer: SnapshotItemContainer<*>? get() = null
    val fluidContainer: SnapshotFluidContainer<*>? get() = null
    val energyContainer: SnapshotEnergyContainer<*>? get() = null
}