package me.ste.library.internals.forge

import me.ste.library.lookup.block.MutableBlockEntityLookup
import me.ste.library.simple.lookup.SimpleItemLookup
import me.ste.library.transfer.energy.EnergyContainerItem
import me.ste.library.transfer.energy.SimulatableEnergyContainer
import me.ste.library.transfer.energy.SnapshotEnergyContainer
import me.ste.library.transfer.fluid.FluidContainerItem
import me.ste.library.transfer.fluid.SimulatableFluidContainer
import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.forge.reverse.*
import me.ste.library.transfer.item.SnapshotItemContainer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import java.util.function.Consumer
import java.util.function.Function

object TransferProvidersImpl {
    private fun <T, R> wrap(level: Level, pos: BlockPos, capability: Capability<T>, adapterFactory: Function<Function<Direction?, T?>, R>): R? {
        val entity = level.getBlockEntity(pos) ?: return null

        val cache = mutableMapOf<Direction?, T?>()

        return adapterFactory.apply {
            cache.computeIfAbsent(it) { side -> entity.getCapability(capability, side).orElse(null) }
        }
    }

    @JvmStatic
    fun getItems(level: Level, pos: BlockPos) = this.wrap(level, pos, ForgeCapabilities.ITEM_HANDLER, ::ItemContainerReverseAdapter)

    @JvmStatic
    fun getFluids(level: Level, pos: BlockPos) = this.wrap(level, pos, ForgeCapabilities.FLUID_HANDLER, ::FluidContainerReverseAdapter)

    @JvmStatic
    fun getEnergy(level: Level, pos: BlockPos) = this.wrap(level, pos, ForgeCapabilities.ENERGY, ::EnergyContainerReverseAdapter)

    @JvmStatic
    fun getFluids(stack: ItemStack, setStack: Consumer<ItemStack>): SimulatableFluidContainer? {
        val handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null) ?: return null
        return FluidContainerItemReverseAdapter({ handler }, setStack)
    }

    @JvmStatic
    fun getEnergy(stack: ItemStack, setStack: Consumer<ItemStack>): SimulatableEnergyContainer? {
        val stackCopy = stack.copy()
        val handler = stackCopy.getCapability(ForgeCapabilities.ENERGY).orElse(null) ?: return null
        return EnergyContainerItemReverseAdapter({ handler }) { setStack.accept(stackCopy.copy()) }
    }

    val ITEMS = mutableListOf<Function<BlockEntity, SnapshotItemContainer<*>?>>()
    val FLUIDS = mutableListOf<Function<BlockEntity, SnapshotFluidContainer<*>?>>()
    val ENERGY = mutableListOf<Function<BlockEntity, SnapshotEnergyContainer<*>?>>()

    val ITEMS_MAP = mutableMapOf<BlockEntityType<*>, Function<BlockEntity, SnapshotItemContainer<*>?>>()
    val FLUIDS_MAP = mutableMapOf<BlockEntityType<*>, Function<BlockEntity, SnapshotFluidContainer<*>?>>()
    val ENERGY_MAP = mutableMapOf<BlockEntityType<*>, Function<BlockEntity, SnapshotEnergyContainer<*>?>>()

    val FLUID_ITEMS = mutableListOf<Function<ItemStack, FluidContainerItem<*>?>>()
    val ENERGY_ITEMS = mutableListOf<Function<ItemStack, EnergyContainerItem<*>?>>()

    val FLUID_ITEMS_MAP = mutableMapOf<Item, Function<ItemStack, FluidContainerItem<*>?>>()
    val ENERGY_ITEMS_MAP = mutableMapOf<Item, Function<ItemStack, EnergyContainerItem<*>?>>()

    @JvmStatic
    fun getItemsRegistry() = object : MutableBlockEntityLookup<SnapshotItemContainer<*>> {
        override fun <E : BlockEntity> registerBlockEntity(
            type: BlockEntityType<E>,
            provider: Function<E, SnapshotItemContainer<*>?>
        ) {
            ITEMS_MAP[type] = provider as Function<BlockEntity, SnapshotItemContainer<*>?>
        }

        override fun registerBlockEntity(provider: Function<BlockEntity, SnapshotItemContainer<*>?>) {
            ITEMS += provider
        }
    }

    @JvmStatic
    fun getFluidsRegistry() = object : MutableBlockEntityLookup<SnapshotFluidContainer<*>> {
        override fun <E : BlockEntity> registerBlockEntity(
            type: BlockEntityType<E>,
            provider: Function<E, SnapshotFluidContainer<*>?>
        ) {
            FLUIDS_MAP[type] = provider as Function<BlockEntity, SnapshotFluidContainer<*>?>
        }

        override fun registerBlockEntity(provider: Function<BlockEntity, SnapshotFluidContainer<*>?>) {
            FLUIDS += provider
        }
    }

    @JvmStatic
    fun getEnergyRegistry() = object : MutableBlockEntityLookup<SnapshotEnergyContainer<*>> {
        override fun <E : BlockEntity> registerBlockEntity(
            type: BlockEntityType<E>,
            provider: Function<E, SnapshotEnergyContainer<*>?>
        ) {
            ENERGY_MAP[type] = provider as Function<BlockEntity, SnapshotEnergyContainer<*>?>
        }

        override fun registerBlockEntity(provider: Function<BlockEntity, SnapshotEnergyContainer<*>?>) {
            ENERGY += provider
        }
    }

    @JvmStatic
    fun registerFluidItem(provider: Function<ItemStack, FluidContainerItem<*>?>) {
        FLUID_ITEMS += provider
    }

    @JvmStatic
    fun registerFluidItem(item: Item, provider: Function<ItemStack, FluidContainerItem<*>?>) {
        FLUID_ITEMS_MAP[item] = provider
    }

    @JvmStatic
    fun registerEnergyItem(provider: Function<ItemStack, EnergyContainerItem<*>?>) {
        ENERGY_ITEMS += provider
    }

    @JvmStatic
    fun registerEnergyItem(item: Item, provider: Function<ItemStack, EnergyContainerItem<*>?>) {
        ENERGY_ITEMS_MAP[item] = provider
    }
}