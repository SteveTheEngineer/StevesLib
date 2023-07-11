package me.ste.library.internals.fabric

import me.ste.library.lookup.block.MutableBlockEntityLookup
import me.ste.library.transfer.fabric.adapter.EnergyContainerFabricAdapter
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import java.util.function.BiFunction
import java.util.function.Function

class FabricContainerRegistry<I, O>(
    private val lookup: BlockApiLookup<O, Direction>,
    private val adapterFactory: BiFunction<I, Direction, O>
) : MutableBlockEntityLookup<I> {
    override fun registerBlockEntity(provider: Function<BlockEntity, I?>) {
        this.lookup.registerFallback { _, _, _, entity, side ->
            if (entity == null) {
                return@registerFallback null
            }

            val container = provider.apply(entity) ?: return@registerFallback null
            this.adapterFactory.apply(container, side)
        }
    }

    override fun <E : BlockEntity> registerBlockEntity(type: BlockEntityType<E>, provider: Function<E, I?>) {
        this.lookup.registerForBlockEntity({ entity, side ->
            val container = provider.apply(entity) ?: return@registerForBlockEntity null
            this.adapterFactory.apply(container, side)
        }, type)
    }

}