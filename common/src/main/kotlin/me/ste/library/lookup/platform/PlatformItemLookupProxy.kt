package me.ste.library.lookup.platform

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.lookup.item.ItemLookupProvider
import me.ste.library.resource.ItemResource
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

class PlatformItemLookupProxy<T> {
    val registrations = mutableListOf<ItemLookupProvider<T>>()
    val itemRegistrations = mutableMapOf<Item, ItemLookupProvider<T>>()

    var platformProvider: ItemLookupProvider<T>? = null
        private set

    fun setPlatformProvider(provider: ItemLookupProvider<T>) {
        if (this.platformProvider != null) {
            throw IllegalStateException("A platform provider has already been set!")
        }

        this.platformProvider = provider
    }

    fun queryItemStack(
        holder: ResourceHolder<ItemResource>
    ): T? {
        return this.itemRegistrations[holder.resource.obj]?.get(holder)
            ?: this.registrations.firstNotNullOfOrNull { it.get(holder) }
    }
}