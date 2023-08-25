package me.ste.library.util

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class BlockStateAndEntitySupplier(
    private val level: Level,
    private val pos: BlockPos,

    private var state: Optional<BlockState>?,
    private var entity: Optional<BlockEntity>?
) {
    fun getBlockState(): BlockState {
        if (this.state == null) {
            val state = this.level.getBlockState(this.pos)
            this.state = Optional.of(state)
            return state
        }

        return this.state!!.get()
    }

    fun getBlockEntity(): BlockEntity? {
        if (this.entity == null) {
            val existingEntity = this.level.getBlockEntity(this.pos)

            if (existingEntity != null) {
                this.entity = Optional.of(existingEntity)
                this.state = Optional.of(existingEntity.blockState)
            } else {
                this.entity = Optional.empty()
            }

            return existingEntity
        }

        return this.entity!!.get()
    }
}