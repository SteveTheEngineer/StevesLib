package me.ste.library.fabric

import me.ste.library.extension.BlockEntityExtensions

interface LevelMixinExtension {
    val newBlockEntities: MutableList<BlockEntityExtensions>
}