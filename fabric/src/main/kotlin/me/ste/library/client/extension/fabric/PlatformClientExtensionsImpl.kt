package me.ste.library.client.extension.fabric

import me.ste.library.client.extension.ItemClientExtensions
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike

object PlatformClientExtensionsImpl {
    val EXTENSIONS = mutableMapOf<Item, ItemClientExtensions>()

    @JvmStatic
    fun registerItemExtensions(item: ItemLike, extensions: ItemClientExtensions) {
        if (extensions.hasDynamicRendering()) {
            BuiltinItemRendererRegistry.INSTANCE.register(item, extensions::renderDynamicItem)
        }

        EXTENSIONS[item.asItem()] = extensions
    }
}