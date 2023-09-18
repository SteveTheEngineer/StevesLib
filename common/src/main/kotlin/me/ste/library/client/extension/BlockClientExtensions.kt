package me.ste.library.client.extension

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

interface BlockClientExtensions {
    companion object {
        private val EXTENSIONS = mutableMapOf<Block, BlockClientExtensions>()

        fun register(block: Block, extensions: BlockClientExtensions) {
            EXTENSIONS[block] = extensions
        }
        fun register(extensions: BlockClientExtensions, vararg blocks: Block) {
            for (block in blocks) {
                register(block, extensions)
            }
        }

        fun getExtensions(block: Block) = EXTENSIONS[block]
        fun getExtensions(state: BlockState) = getExtensions(state.block)
    }

    fun getParticleIcon(state: BlockState, level: ClientLevel?, pos: BlockPos?): TextureAtlasSprite? = null
}