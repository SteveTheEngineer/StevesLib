package me.ste.library.resource

import me.ste.library.StevesLib
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.function.Consumer

data class ItemResource(
    override val obj: Item,
    override val tag: CompoundTag? = null
) : VanillaResource<Item> {
    constructor(stack: ItemStack) : this(stack.item, stack.tag)

    override val isEmpty = this.obj == Items.AIR

    override fun withTag(tagConsumer: Consumer<CompoundTag>): ItemResource {
        val newTag = this.tag?.copy() ?: CompoundTag()
        tagConsumer.accept(newTag)
        return ItemResource(this.obj, newTag)
    }

    fun isSameObject(other: ItemStack) = other.item == this.obj
    fun isSameTag(other: ItemStack) = other.tag == this.tag
    fun isSame(other: ItemStack) = this.isSameObject(other) && this.isSameTag(other)

    fun toStack(amount: Int = 1): ItemStack {
        if (this.isEmpty || amount <= 0) {
            return ItemStack.EMPTY
        }

        val stack = ItemStack(this.obj, amount)
        stack.tag = this.tag?.copy()
        return stack
    }

    fun write(buf: FriendlyByteBuf) {
        if (this.isEmpty) {
            buf.writeBoolean(false)
            return
        }

        buf.writeBoolean(true)
        buf.writeId(BuiltInRegistries.ITEM, this.obj)
        buf.writeNbt(this.tag)
    }

    fun save(): CompoundTag {
        val out = CompoundTag()

        val id = StevesLib.ITEM_REGISTRY.getId(this.obj)
        out.putString("id", id?.toString() ?: "minecraft:air")

        if (this.tag != null) {
            out.put("tag", this.tag.copy())
        }

        return out
    }

    companion object {
        val EMPTY = ItemResource(Items.AIR)

        fun read(buf: FriendlyByteBuf): ItemResource {
            if (buf.readBoolean()) {
                return EMPTY
            }

            val item = buf.readById(BuiltInRegistries.ITEM) ?: return EMPTY
            val tag = buf.readNbt()

            return ItemResource(item, tag)
        }

        fun load(tag: CompoundTag): ItemResource {
            try {
                return ItemResource(
                    obj = StevesLib.ITEM_REGISTRY.get(
                        ResourceLocation(
                            tag.getString("id")
                        )
                    )!!,

                    tag = if (tag.contains("tag", Tag.TAG_COMPOUND.toInt())) {
                        tag.getCompound("tag")
                    } else {
                        null
                    }
                )
            } catch (t: Throwable) {
                return EMPTY
            }
        }
    }
}