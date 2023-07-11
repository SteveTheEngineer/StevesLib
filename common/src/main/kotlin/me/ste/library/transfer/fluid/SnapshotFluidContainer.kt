package me.ste.library.transfer.fluid

import me.ste.library.transfer.base.SnapshotProvider

interface SnapshotFluidContainer<S> : FluidContainer, SnapshotProvider<S>