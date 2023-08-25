package me.ste.library.lookup.item

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.ItemResource
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

fun interface ItemLookupProvider<T> {
    fun get(holder: ResourceHolder<ItemResource>): T?
}