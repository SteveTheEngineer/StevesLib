package me.ste.library.transfer

import me.ste.library.lookup.block.BlockLookup
import me.ste.library.lookup.block.MutableBlockEntityLookup

class TransferBlockLookup<I, O>(
    inCallback: BlockLookup<O>,
    outCallback: MutableBlockEntityLookup<I>
) : BlockLookup<O> by inCallback, MutableBlockEntityLookup<I> by outCallback