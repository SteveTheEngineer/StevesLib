package me.ste.library.registry

import dev.architectury.registry.registries.Registrar
import net.minecraft.core.IdMap

class RegistrarIdMap<T>(private val registrar: Registrar<T>) : IdMap<T> {
    override fun iterator() = this.registrar.iterator()

    override fun byId(id: Int) = this.registrar.byRawId(id)

    override fun size() = this.registrar.ids.size

    override fun getId(value: T) = this.registrar.getRawId(value)
}