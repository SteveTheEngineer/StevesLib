package me.ste.library.transfer.resource

import dev.architectury.fluid.FluidStack
import dev.architectury.registry.registries.Registries
import me.ste.library.StevesLib
import me.ste.library.hooks.ExtraFluidStackHooks
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import java.util.Objects

data class StackableFluid(
    val fluid: Fluid,
    val tag: CompoundTag? = null
) {
    companion object {
        val EMPTY = StackableFluid(Fluids.EMPTY)

        fun load(tag: CompoundTag): StackableFluid {
            if (!tag.contains("Id", Tag.TAG_STRING.toInt())) {
                return EMPTY
            }

            val keyString = tag.getString("Id")

            val key = ResourceLocation.tryParse(keyString)
                ?: return EMPTY

            val fluid = Registries.get(StevesLib.MOD_ID).get(Registry.FLUID_REGISTRY).get(key)
                ?: return EMPTY

            if (fluid == Fluids.EMPTY) {
                return EMPTY
            }

            val fluidTag = if (tag.contains("Data", Tag.TAG_COMPOUND.toInt())) tag.getCompound("Data") else null

            return StackableFluid(fluid, fluidTag)
        }

        fun read(buf: FriendlyByteBuf): StackableFluid {
            if (!buf.readBoolean()) {
                return EMPTY
            }

            val fluid = buf.readById(Registry.FLUID)
                ?: return EMPTY

            val fluidTag = buf.readNbt()

            return StackableFluid(fluid, fluidTag)
        }
    }

    constructor(stack: FluidStack) : this(
        stack.fluid,
        stack.tag
    )

    val isEmpty = this.fluid == Fluids.EMPTY
    val bucketFluidStack = this.toStack(FluidStack.bucketAmount())

    val isLighterThanAir = ExtraFluidStackHooks.isLighterThanAir(this.bucketFluidStack)
    val fillSound = ExtraFluidStackHooks.getFillSound(this.bucketFluidStack)
    val emptySound = ExtraFluidStackHooks.getEmptySound(this.bucketFluidStack)

    fun sameFluid(other: StackableFluid) = other.fluid == this.fluid
    fun sameTag(other: StackableFluid) = other.tag == this.tag

    fun sameFluid(other: FluidStack) = other.fluid == this.fluid
    fun sameTag(other: FluidStack) = other.tag == this.tag

    fun sameFluidSameTag(other: FluidStack) = this.sameFluid(other) && this.sameTag(other)

    fun save(tag: CompoundTag) {
        val key = Registries.get(StevesLib.MOD_ID).get(Registry.FLUID_REGISTRY).getKey(this.fluid)

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
        buf.writeId(Registry.FLUID, this.fluid)
        buf.writeNbt(this.tag)
    }

    fun toStack(amount: Long): FluidStack {
        if (this.isEmpty) {
            return FluidStack.empty()
        }

        return FluidStack.create(this.fluid, amount, this.tag)
    }

    override fun hashCode() = Objects.hash(this.fluid, this.tag)
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is StackableFluid) {
            return false
        }

        return other.fluid == this.fluid && other.tag == this.tag
    }

    override fun toString() = "StackableFluid[${this.fluid}${this.tag}]"
}