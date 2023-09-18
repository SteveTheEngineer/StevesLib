package me.ste.library.simple.lookup

import me.ste.library.lookup.block.BlockLookup
import me.ste.library.lookup.block.BlockLookupProvider
import me.ste.library.lookup.block.MutableBlockLookup
import me.ste.library.util.BlockStateAndEntitySupplier
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class SimpleBlockLookup<T> : BlockLookup<T>, MutableBlockLookup<T> {
    private val registrations = mutableListOf<BlockLookupProvider<T, BlockEntity>>()

    override fun get(
        level: Level,
        pos: BlockPos,
        side: Direction?,
        state: Optional<BlockState>?,
        entity: Optional<BlockEntity>?
    ): T? {
        val supplier = BlockStateAndEntitySupplier(level, pos, state, entity)
        
        return this.registrations.firstNotNullOfOrNull { it.get(level, pos, side, supplier::getBlockState, supplier::getBlockEntity) }
    }

    override fun register(provider: BlockLookupProvider<T, BlockEntity>) {
        this.registrations += provider
    }

    override fun registerBlock(block: Block, provider: BlockLookupProvider<T, BlockEntity>) {
        this.register { level, pos, side, lazyState, lazyEntity ->
            val state = lazyState.get()
            
            if (state.block != block) {
                return@register null
            }
            
            return@register provider.get(level, pos, side, { state }, lazyEntity)
        }
    }

    override fun <E : BlockEntity> registerBlockEntity(type: BlockEntityType<E>, provider: BlockLookupProvider<T, E>) {
        this.register { level, pos, side, lazyState, lazyEntity ->
            val entity = lazyEntity.get() ?: return@register null

            if (entity.type != type) {
                return@register null
            }

            return@register provider.get(level, pos, side, lazyState, { entity as E })
        }
    }
}