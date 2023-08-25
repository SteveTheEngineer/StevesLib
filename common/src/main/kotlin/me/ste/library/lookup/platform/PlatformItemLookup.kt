package me.ste.library.lookup.platform

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.simple.SingleSlotConsumer
import me.ste.library.lookup.item.ItemLookup
import me.ste.library.lookup.item.ItemLookupProvider
import me.ste.library.lookup.item.MutableItemLookup
import me.ste.library.resource.ItemResource
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

class PlatformItemLookup<T>(
    private val proxy: PlatformItemLookupProxy<T>
) : ItemLookup<T>, MutableItemLookup<T> {
    override fun getReadOnly(stack: ItemStack) = this.get(stack) {}

    override fun get(stack: ItemStack, setStack: Consumer<ItemStack>) = this.get(SingleSlotConsumer(stack, setStack))

    override fun get(holder: ResourceHolder<ItemResource>): T? {
        val provider = this.proxy.platformProvider
            ?: throw IllegalStateException("No platform provider is registered.")

        return provider.get(holder)
    }

    override fun register(provider: ItemLookupProvider<T>) {
        this.proxy.registrations += provider
    }

    override fun registerItem(item: Item, provider: ItemLookupProvider<T>) {
        this.proxy.itemRegistrations[item] = provider
    }
}