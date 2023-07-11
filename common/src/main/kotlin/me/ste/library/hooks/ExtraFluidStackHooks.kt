package me.ste.library.hooks

import dev.architectury.fluid.FluidStack
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.sounds.SoundEvent

object ExtraFluidStackHooks {
    @JvmStatic
    @ExpectPlatform
    fun getFillSound(stack: FluidStack): SoundEvent {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun getEmptySound(stack: FluidStack): SoundEvent {
        throw UnsupportedOperationException()
    }

    @JvmStatic
    @ExpectPlatform
    fun isLighterThanAir(stack: FluidStack): Boolean {
        throw UnsupportedOperationException()
    }
}