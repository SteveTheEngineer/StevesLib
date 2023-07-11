package me.ste.library.transfer.resource

import dev.architectury.registry.registries.Registries
import me.ste.library.StevesLib
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.Objects

data class StackableItem(
    val item: Item,
    val tag: CompoundTag? = null
) {
    companion object {
        val EMPTY = StackableItem(Items.AIR)

        fun load(tag: CompoundTag): StackableItem {
            if (!tag.contains("Id", Tag.TAG_STRING.toInt())) {
                return EMPTY
            }

            val keyString = tag.getString("Id")

            val key = ResourceLocation.tryParse(keyString)
                ?: return EMPTY

            val item = Registries.get(StevesLib.MOD_ID).get(Registry.ITEM_REGISTRY).get(key)
                ?: return EMPTY

            val itemTag = if (tag.contains("Data", Tag.TAG_COMPOUND.toInt())) tag.getCompound("Data") else null

            return StackableItem(item, itemTag)
        }

        fun read(buf: FriendlyByteBuf): StackableItem {
            if (!buf.readBoolean()) {
                return EMPTY
            }

            val item = buf.readById(Registry.ITEM)
                ?: return EMPTY

            val itemTag = buf.readNbt()

            return StackableItem(item, itemTag)
        }
    }

    constructor(stack: ItemStack) : this(
        stack.item,
        stack.tag
    )

    val isEmpty = this.item == Items.AIR

    fun sameItem(other: StackableItem) = other.item == this.item
    fun sameTag(other: StackableItem) = other.tag == this.tag

    fun sameItem(other: ItemStack) = other.item == this.item
    fun sameTag(other: ItemStack) = other.tag == this.tag

    fun sameItemSameTag(other: ItemStack) = this.sameItem(other) && this.sameTag(other)

    fun save(tag: CompoundTag) {
        val key = Registries.get(StevesLib.MOD_ID).get(Registry.ITEM_REGISTRY).getKey(this.item)

        tag.putString("Id", key.toString())

        if (this.tag != null) {
            tag.put("Data", this.tag)
        }
    }

    fun write(buf: FriendlyByteBuf) {
        if (this.isEmpty) {
            buf.writeBoolean(false)
            return
        }

        buf.writeBoolean(true)
        buf.writeId(Registry.ITEM, this.item)
        buf.writeNbt(this.tag)
    }

    fun toStack(amount: Int): ItemStack {
        if (this.isEmpty) {
            return ItemStack.EMPTY
        }

        val stack = ItemStack(this.item, amount)
        stack.tag = this.tag
        return stack
    }

    override fun hashCode() = Objects.hash(this.item, this.tag)
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is StackableItem) {
            return false
        }

        return other.item == this.item && other.tag == this.tag
    }

    override fun toString() = "StackableItem[${this.item}${this.tag}]"
}