package me.ste.library.hooks.forge

import dev.architectury.fluid.FluidStack
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraftforge.common.SoundActions

object ExtraFluidStackHooksImpl {
    @JvmStatic
    fun getFillSound(stack: FluidStack): SoundEvent {
        val forgeStack = FluidStackHooksForge.toForge(stack)
        return forgeStack.fluid.fluidType.getSound(forgeStack, SoundActions.BUCKET_FILL)
            ?: SoundEvents.BUCKET_FILL
    }

    @JvmStatic
    fun getEmptySound(stack: FluidStack): SoundEvent {
        val forgeStack = FluidStackHooksForge.toForge(stack)
        return forgeStack.fluid.fluidType.getSound(forgeStack, SoundActions.BUCKET_EMPTY)
            ?: SoundEvents.BUCKET_EMPTY
    }

    @JvmStatic
    fun isLighterThanAir(stack: FluidStack): Boolean {
        val forgeStack = FluidStackHooksForge.toForge(stack)
        return forgeStack.fluid.fluidType.getDensity(forgeStack) <= 0
    }
}