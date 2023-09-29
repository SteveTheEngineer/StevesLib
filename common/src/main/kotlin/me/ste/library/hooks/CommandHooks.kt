package me.ste.library.hooks

import com.mojang.brigadier.arguments.ArgumentType
import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation

object CommandHooks {
    @JvmStatic
    @ExpectPlatform
    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerArgument(id: ResourceLocation, clazz: Class<A>, serializer: ArgumentTypeInfo<A, T>) {
        throw UnsupportedOperationException()
    }
}