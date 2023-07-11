package me.ste.library.simple.lookup

import me.ste.library.lookup.block.*
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import java.util.function.Function

class SimpleBlockLookup<T> : BlockLookup<T>, MutableBlockLookup<T>, MutableBlockEntityLookup<T> {
    private val providers = mutableListOf<BlockLookupProvider<T>>()

    private val blockMap = mutableMapOf<Block, BlockStateLookupProvider<T>>()
    private val blocks = mutableListOf<BlockStateLookupProvider<T>>()

    private val blockEntitiesMap = mutableMapOf<BlockEntityType<*>, Function<BlockEntity, T?>>()
    private val blockEntities = mutableListOf<Function<BlockEntity, T?>>()

    override fun get(level: Level, pos: BlockPos) = this.providers.firstNotNullOfOrNull { it.get(level, pos) }
    
    override fun register(provider: BlockLookupProvider<T>) {
        this.providers += provider
    }

    override fun registerBlockState(provider: BlockStateLookupProvider<T>) {
        this.blocks += provider
    }

    override fun registerBlockState(block: Block, provider: BlockStateLookupProvider<T>) {
        this.blockMap[block] = provider
    }

    override fun registerBlockEntity(provider: Function<BlockEntity, T?>) {
        this.blockEntities += provider
    }

    override fun <E : BlockEntity> registerBlockEntity(type: BlockEntityType<E>, provider: Function<E, T?>) {
        this.blockEntitiesMap[type] = provider as Function<BlockEntity, T?>
    }

    init {
        this.register { level, pos ->
            val entity = level.getBlockEntity(pos) ?: return@register null
            this.blockEntitiesMap[entity.type]?.apply(entity)
                ?: this.blockEntities.firstNotNullOfOrNull { it.apply(entity) }
        }

        this.register { level, pos ->
            val state = level.getBlockState(pos)
            this.blockMap[state.block]?.get(level, pos, state)
                ?: this.blocks.firstNotNullOfOrNull { it.get(level, pos, state) }
        }
    }
}