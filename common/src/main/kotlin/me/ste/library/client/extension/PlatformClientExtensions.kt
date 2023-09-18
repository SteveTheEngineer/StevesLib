package me.ste.library.client.extension

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.world.level.ItemLike

object PlatformClientExtensions {
    @JvmStatic
    @ExpectPlatform
    fun registerItemExtensions(item: ItemLike, extensions: ItemClientExtensions) {
        throw UnsupportedOperationException()
    }

    fun registerItemExtensions(extensions: ItemClientExtensions, vararg items: ItemLike) {
        for (item in items) {
            this.registerItemExtensions(item, extensions)
        }
    }
}