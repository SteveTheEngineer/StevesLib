package me.ste.library.hooks.fabric

import com.mojang.brigadier.arguments.ArgumentType
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation

object CommandHooksImpl {
    @JvmStatic
    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerArgument(id: ResourceLocation, clazz: Class<A>, serializer: ArgumentTypeInfo<A, T>) {
        ArgumentTypeRegistry.registerArgumentType(id, clazz, serializer)
    }
}