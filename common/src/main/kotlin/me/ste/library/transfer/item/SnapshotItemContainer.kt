package me.ste.library.transfer.item

import me.ste.library.transfer.base.SnapshotProvider

interface SnapshotItemContainer<S> : ItemContainer, SnapshotProvider<S>