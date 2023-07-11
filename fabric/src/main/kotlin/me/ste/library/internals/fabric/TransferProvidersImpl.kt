package me.ste.library.internals.fabric

import me.ste.library.lookup.block.MutableBlockEntityLookup
import me.ste.library.lookup.item.ItemLookupProvider
import me.ste.library.lookup.item.MutableItemLookup
import me.ste.library.transfer.energy.EnergyContainerItem
import me.ste.library.transfer.energy.SnapshotEnergyContainer
import me.ste.library.transfer.fabric.ReverseAdapterSingleSlotStorage
import me.ste.library.transfer.fabric.adapter.*
import me.ste.library.transfer.fabric.reverse.EnergyContainerReverseAdapter
import me.ste.library.transfer.fabric.reverse.FluidContainerReverseAdapter
import me.ste.library.transfer.fabric.reverse.ItemContainerReverseAdapter
import me.ste.library.transfer.fluid.FluidContainerItem
import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.item.SnapshotItemContainer
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import team.reborn.energy.api.EnergyStorage
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

object TransferProvidersImpl {
    private fun <T, R> wrap(level: Level, pos: BlockPos, lookup: BlockApiLookup<T, Direction>, adapterFactory: Function<Function<Direction?, T?>, R>): R? {
        val cache = mutableMapOf<Direction, T?>()

        return adapterFactory.apply {
            cache.computeIfAbsent(it ?: Direction.UP) { side -> lookup.find(level, pos, side) }
        }
    }

    private fun <T, R> wrap(stack: ItemStack, setStack: Consumer<ItemStack>, lookup: ItemApiLookup<T, ContainerItemContext>, adapterFactory: Function<Function<Direction?, T?>, R>): R? {
        val context = ContainerItemContext.ofSingleSlot(
            ReverseAdapterSingleSlotStorage(stack, setStack)
        )

        val storage = lookup.find(stack, context) ?: return null

        return adapterFactory.apply { storage }
    }

    private fun <I, O> registerItem(lookup: ItemApiLookup<O, ContainerItemContext>, adapterFactory: BiFunction<ContainerItemContext, I, O?>, provider: Function<ItemStack, I?>) {
        lookup.registerFallback { stack, context ->
            val container = provider.apply(stack) ?: return@registerFallback null
            adapterFactory.apply(context, container)
        }
    }

    private fun <I, O> registerItem(lookup: ItemApiLookup<O, ContainerItemContext>, adapterFactory: BiFunction<ContainerItemContext, I, O?>, provider: Function<ItemStack, I?>, item: Item) {
        lookup.registerForItems({ stack, context ->
            val container = provider.apply(stack) ?: return@registerForItems null
            adapterFactory.apply(context, container)
        }, item)
    }

    @JvmStatic
    fun getItems(level: Level, pos: BlockPos) = this.wrap(level, pos, ItemStorage.SIDED, ::ItemContainerReverseAdapter)

    @JvmStatic
    fun getFluids(level: Level, pos: BlockPos) = this.wrap(level, pos, FluidStorage.SIDED, ::FluidContainerReverseAdapter)

    @JvmStatic
    fun getEnergy(level: Level, pos: BlockPos) = this.wrap(level, pos, EnergyStorage.SIDED, ::EnergyContainerReverseAdapter)

    @JvmStatic
    fun getFluids(stack: ItemStack, setStack: Consumer<ItemStack>) = this.wrap(stack, setStack, FluidStorage.ITEM, ::FluidContainerReverseAdapter)

    @JvmStatic
    fun getEnergy(stack: ItemStack, setStack: Consumer<ItemStack>) = this.wrap(stack, setStack, EnergyStorage.ITEM, ::EnergyContainerReverseAdapter)

    @JvmStatic
    fun getItemsRegistry() = FabricContainerRegistry(ItemStorage.SIDED, ::ItemContainerFabricAdapter)

    @JvmStatic
    fun getFluidsRegistry() = FabricContainerRegistry(FluidStorage.SIDED, ::FluidContainerFabricAdapter)

    @JvmStatic
    fun getEnergyRegistry() = FabricContainerRegistry(EnergyStorage.SIDED, ::EnergyContainerFabricAdapter)

    @JvmStatic
    fun registerFluidItem(provider: Function<ItemStack, FluidContainerItem<*>?>) {
        this.registerItem(FluidStorage.ITEM, ::FluidContainerFabricItemAdapter, provider)
    }

    @JvmStatic
    fun registerFluidItem(item: Item, provider: Function<ItemStack, FluidContainerItem<*>?>) {
        this.registerItem(FluidStorage.ITEM, ::FluidContainerFabricItemAdapter, provider, item)
    }

    @JvmStatic
    fun registerEnergyItem(provider: Function<ItemStack, EnergyContainerItem<*>?>) {
        this.registerItem(EnergyStorage.ITEM, ::EnergyContainerFabricItemAdapter, provider)
    }

    @JvmStatic
    fun registerEnergyItem(item: Item, provider: Function<ItemStack, EnergyContainerItem<*>?>) {
        this.registerItem(EnergyStorage.ITEM, ::EnergyContainerFabricItemAdapter, provider, item)
    }
}