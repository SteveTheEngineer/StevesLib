package me.ste.library.container.forge.reverse

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import me.ste.library.container.forge.simulation.SimulatedItemSlot
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionResult
import me.ste.library.transaction.TransactionShard

class FluidHolderReverseAdapter(
    private val parent: FluidContainerReverseAdapter,
    override val slot: Int,
    private val amountChange: Int
) : ResourceHolder<FluidResource> {
    override val resource get() = FluidResource(
        FluidStackHooksForge.fromForge(
            this.parent.storage.getFluidInTank(this.slot)
        )
    )

    override val amount get() = (this.parent.storage.getFluidInTank(this.slot).amount + this.amountChange).toLong()

    override val capacity get() = this.parent.storage.getTankCapacity(this.slot).toLong()

    override val isEmpty: Boolean get() {
        val fluid = this.parent.storage.getFluidInTank(this.slot)

        if (fluid.isEmpty) {
            return true
        }

        return (fluid.amount + this.amountChange) <= 0
    }

    override fun accept(resource: FluidResource, amount: Long, transaction: TransactionShard) = 0L

    override fun output(amount: Long, transaction: TransactionShard) = 0L

    override val canAccept = false

    override val canOutput = false

    override fun canOutput(resource: FluidResource) = this.canOutput

    override fun canAccept(resource: FluidResource) = this.parent.storage.isFluidValid(this.slot, FluidStackHooksForge.toForge(resource.toStack()))
}