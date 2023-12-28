package me.ste.library.resource

import dev.architectury.fluid.FluidStack
import dev.architectury.registry.registries.RegistrarManager
import me.ste.library.StevesLib
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import java.util.function.Consumer

data class FluidResource(
    override val obj: Fluid,
    override val tag: CompoundTag? = null
) : VanillaResource<Fluid> {
    constructor(stack: FluidStack) : this(stack.fluid, stack.tag)

    override val isEmpty = this.obj == Fluids.EMPTY

    override fun withTag(tagConsumer: Consumer<CompoundTag>): FluidResource {
        val newTag = this.tag?.copy() ?: CompoundTag()
        tagConsumer.accept(newTag)
        return FluidResource(this.obj, newTag)
    }

    fun isSameObject(other: FluidStack) = other.fluid == this.obj
    fun isSameTag(other: FluidStack) = other.tag == this.tag
    fun isSame(other: FluidStack) = this.isSameObject(other) && this.isSameTag(other)

    fun toStack(amount: Long = FluidStack.bucketAmount()): FluidStack {
        if (this.isEmpty || amount == 0L) {
            return FluidStack.empty()
        }

        return FluidStack.create(this.obj, amount, this.tag?.copy())
    }

    fun write(buf: FriendlyByteBuf) {
        if (this.isEmpty) {
            buf.writeBoolean(false)
            return
        }

        buf.writeBoolean(true)
        buf.writeId(BuiltInRegistries.FLUID, this.obj)
        buf.writeNbt(this.tag)
    }

    fun save(): CompoundTag {
        val out = CompoundTag()

        val id = StevesLib.FLUID_REGISTRY.getId(this.obj)
        out.putString("id", id?.toString() ?: "minecraft:empty")

        if (this.tag != null) {
            out.put("tag", this.tag.copy())
        }

        return out
    }

    companion object {
        val EMPTY = FluidResource(Fluids.EMPTY)

        fun read(buf: FriendlyByteBuf): FluidResource {
            if (buf.readBoolean()) {
                return EMPTY
            }

            val fluid = buf.readById(BuiltInRegistries.FLUID) ?: return EMPTY
            val tag = buf.readNbt()

            return FluidResource(fluid, tag)
        }

        fun load(tag: CompoundTag): FluidResource {
            try {
                return FluidResource(
                    obj = StevesLib.FLUID_REGISTRY.get(
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