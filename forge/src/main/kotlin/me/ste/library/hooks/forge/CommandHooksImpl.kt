package me.ste.library.hooks.forge

import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.resources.ResourceLocation

object CommandHooksImpl {
    @JvmStatic
    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerArgument(id: ResourceLocation, clazz: Class<A>, serializer: ArgumentTypeInfo<A, T>) {
        ArgumentTypeInfos.registerByClass(clazz, serializer)
    }
}