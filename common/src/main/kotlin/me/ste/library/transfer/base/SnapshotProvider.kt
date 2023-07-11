package me.ste.library.transfer.base

import net.minecraft.core.Direction

interface SnapshotProvider<S> {
    fun createSnapshot(side: Direction?): S
    fun readSnapshot(side: Direction?, snapshot: S)
    fun saveChanges(side: Direction?)
}