package me.ste.library.lookup.platform

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

class PlatformBlockLookup<T>(
    private val proxy: PlatformBlockLookupProxy<T>
) : BlockLookup<T>, MutableBlockLookup<T> {
    override fun get(
        level: Level,
        pos: BlockPos,
        side: Direction?,
        state: Optional<BlockState>?,
        entity: Optional<BlockEntity>?
    ): T? {
        val provider = this.proxy.platformProvider
            ?: throw IllegalStateException("No platform provider is registered.")

        val supplier = BlockStateAndEntitySupplier(level, pos, state, entity)

        return provider.get(level, pos, side, supplier::getBlockState, supplier::getBlockEntity)
    }

    override fun register(provider: BlockLookupProvider<T, BlockEntity>) {
        this.proxy.registrations += provider
    }

    override fun registerBlock(block: Block, provider: BlockLookupProvider<T, BlockEntity>) {
        this.proxy.blockRegistrations[block] = provider
    }

    override fun <E : BlockEntity> registerBlockEntity(
        type: BlockEntityType<E>,
        provider: BlockLookupProvider<T, E>
    ) {
        this.proxy.blockEntityRegistrations[type] = provider as BlockLookupProvider<T, BlockEntity>
    }
}