package me.ste.library.lookup.platform

import me.ste.library.lookup.block.BlockLookupProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Supplier

class PlatformBlockLookupProxy<T> {
    val registrations = mutableListOf<BlockLookupProvider<T, BlockEntity>>()
    val blockRegistrations = mutableMapOf<Block, BlockLookupProvider<T, BlockEntity>>()
    val blockEntityRegistrations = mutableMapOf<BlockEntityType<*>, BlockLookupProvider<T, BlockEntity>>()

    var platformProvider: BlockLookupProvider<T, BlockEntity>? = null
        private set

    fun setPlatformProvider(provider: BlockLookupProvider<T, BlockEntity>) {
        if (this.platformProvider != null) {
            throw IllegalStateException("A platform provider has already been set!")
        }

        this.platformProvider = provider
    }

    fun queryBlock(
        level: Level,
        pos: BlockPos,
        side: Direction?,
        state: BlockState,
        entity: BlockEntity?
    ): T? {
        val stateSupplier = Supplier { state }
        val entitySupplier = Supplier { entity }

        return this.blockEntityRegistrations[entity?.type]?.get(level, pos, side, stateSupplier, entitySupplier)
            ?: this.blockRegistrations[state.block]?.get(level, pos, side, stateSupplier, entitySupplier)
            ?: this.registrations.firstNotNullOfOrNull { it.get(level, pos, side, stateSupplier, entitySupplier) }
    }
}