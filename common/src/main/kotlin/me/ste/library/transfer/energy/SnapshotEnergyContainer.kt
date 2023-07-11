package me.ste.library.transfer.energy

import me.ste.library.transfer.base.SnapshotProvider

interface SnapshotEnergyContainer<S> : EnergyContainer, SnapshotProvider<S>