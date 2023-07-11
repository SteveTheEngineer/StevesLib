package me.ste.library.hooks.fabric

import dev.architectury.fluid.FluidStack
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
import net.minecraft.sounds.SoundEvent

object ExtraFluidStackHooksImpl {
    @JvmStatic
    fun getFillSound(stack: FluidStack): SoundEvent {
        val variant = FluidStackHooksFabric.toFabric(stack)
        return FluidVariantAttributes.getFillSound(variant)
    }

    @JvmStatic
    fun getEmptySound(stack: FluidStack): SoundEvent {
        val variant = FluidStackHooksFabric.toFabric(stack)
        return FluidVariantAttributes.getEmptySound(variant)
    }

    @JvmStatic
    fun isLighterThanAir(stack: FluidStack): Boolean {
        val variant = FluidStackHooksFabric.toFabric(stack)
        return FluidVariantAttributes.isLighterThanAir(variant)
    }
}