package me.ste.library.util

import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey

object RegistryUtils {
    fun <T> builder(manager: RegistrarManager, key: ResourceKey<Registry<T>>) = manager.builder<T>(key.location())
    fun <T> vanilla(key: ResourceKey<Registry<T>>) = BuiltInRegistries.REGISTRY.get(key.location()) as Registry<T>
}