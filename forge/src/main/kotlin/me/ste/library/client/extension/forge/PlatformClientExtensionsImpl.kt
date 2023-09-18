package me.ste.library.client.extension.forge

import me.ste.library.client.extension.ItemClientExtensions
import me.ste.library.forge.mixin.common.ItemAccessor
import net.minecraft.world.level.ItemLike

object PlatformClientExtensionsImpl {
    @JvmStatic
    fun registerItemExtensions(item: ItemLike, extensions: ItemClientExtensions) {
        (item as ItemAccessor).setRenderProperties(
            ItemClientExtensionsAdapter(extensions)
        )
    }
}