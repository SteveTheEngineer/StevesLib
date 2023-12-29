package me.ste.library.util

import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey

object RegistryUtils {
    fun <T> builder(manager: RegistrarManager, key: ResourceKey<Registry<T>>) = manager.builder<T>(key.location())
}