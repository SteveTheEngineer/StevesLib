package me.ste.library.simple.lookup

import me.ste.library.container.resource.ResourceHolder
import me.ste.library.lookup.item.ItemLookup
import me.ste.library.lookup.item.ItemLookupProvider
import me.ste.library.lookup.item.MutableItemLookup
import me.ste.library.resource.ItemResource
import me.ste.library.simple.SingleSlotConsumer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

class SimpleItemLookup<T> : ItemLookup<T>, MutableItemLookup<T> {
    private val registrations = mutableListOf<ItemLookupProvider<T>>()
    private val itemRegistrations = mutableMapOf<Item, ItemLookupProvider<T>>()

    override fun getReadOnly(stack: ItemStack) =
        this.get(stack) {}

    override fun get(stack: ItemStack, setStack: Consumer<ItemStack>) =
        this.get(SingleSlotConsumer(stack, setStack))

    override fun get(holder: ResourceHolder<ItemResource>): T? {
        val item = holder.resource.obj

        val itemRegistration = this.itemRegistrations[item]
        if (itemRegistration != null) {
            val obj = itemRegistration.get(holder)
            if (obj != null) {
                return obj
            }
        }

        return this.registrations.firstNotNullOfOrNull { it.get(holder) }
    }

    override fun register(provider: ItemLookupProvider<T>) {
        this.registrations += provider
    }

    override fun registerItem(item: Item, provider: ItemLookupProvider<T>) {
        this.itemRegistrations[item] = provider
    }
}