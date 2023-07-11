package me.ste.library.internals

import dev.architectury.injectables.annotations.ExpectPlatform
import me.ste.library.lookup.block.MutableBlockEntityLookup
import me.ste.library.lookup.item.MutableItemLookup
import me.ste.library.transfer.energy.EnergyContainerItem
import me.ste.library.transfer.energy.SimulatableEnergyContainer
import me.ste.library.transfer.energy.SnapshotEnergyContainer
import me.ste.library.transfer.fluid.FluidContainerItem
import me.ste.library.transfer.fluid.SimulatableFluidContainer
import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.item.SimulatableItemContainer
import me.ste.library.transfer.item.SnapshotItemContainer
import net.minecraft.core.BlockPos
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import java.util.function.Consumer
import java.util.function.Function

internal object TransferProviders {
    @JvmStatic
    @ExpectPlatform
    fun getItems(level: Level, pos: BlockPos): SimulatableItemContainer? {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun getFluids(level: Level, pos: BlockPos): SimulatableFluidContainer? {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun getEnergy(level: Level, pos: BlockPos): SimulatableEnergyContainer? {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun getFluids(stack: ItemStack, setStack: Consumer<ItemStack>): SimulatableFluidContainer? {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun getEnergy(stack: ItemStack, setStack: Consumer<ItemStack>): SimulatableEnergyContainer? {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun getItemsRegistry(): MutableBlockEntityLookup<SnapshotItemContainer<*>> {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun getFluidsRegistry(): MutableBlockEntityLookup<SnapshotFluidContainer<*>> {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun getEnergyRegistry(): MutableBlockEntityLookup<SnapshotEnergyContainer<*>> {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun registerFluidItem(provider: Function<ItemStack, FluidContainerItem<*>?>) {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun registerFluidItem(item: Item, provider: Function<ItemStack, FluidContainerItem<*>?>) {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun registerEnergyItem(provider: Function<ItemStack, EnergyContainerItem<*>?>) {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun registerEnergyItem(item: Item, provider: Function<ItemStack, EnergyContainerItem<*>?>) {
        throw UnsupportedOperationException()
    }
}