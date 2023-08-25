package me.ste.library.simple.slot.fluid

import dev.architectury.fluid.FluidStack
import dev.architectury.utils.Amount
import me.ste.library.container.SnapshotHolder
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.container.slotted.SlottedFluidContainer
import me.ste.library.resource.FluidResource
import me.ste.library.resource.ItemResource
import me.ste.library.transaction.TransactionShard
import net.minecraft.world.item.ItemStack

class SlottedFluidContainerHolder(
    protected val container: SlottedFluidContainer,
    override val slot: Int
) : ResourceHolder<FluidResource> {
    private val snapshots = SnapshotHolder(this::fluid, { this.container.setFluid(this.slot, it) }) {}

    protected var fluid: FluidStack
        get() = this.container.getFluid(this.slot)
        set(value) { this.container.setFluid(this.slot, value) }

    override val resource = FluidResource(this.fluid)

    override val amount = this.fluid.amount

    override val capacity = this.container.getCapacity(this.slot)

    override val isEmpty = this.fluid.isEmpty

    override fun accept(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        this.snapshots.track(transaction)

        val currentFluid = this.fluid
        if (!currentFluid.isEmpty && !resource.isSame(currentFluid)) {
            return 0L
        }

        val acceptedStack = resource.toStack(amount)
        val max = this.container.getCapacity(this.slot)

        val toAccept = acceptedStack.amount.coerceAtMost(max - currentFluid.amount)
        if (toAccept <= 0) {
            return 0L
        }

        acceptedStack.amount = currentFluid.amount + toAccept
        this.fluid = acceptedStack
        return toAccept.toLong()
    }

    override fun output(amount: Long, transaction: TransactionShard): Long {
        this.snapshots.track(transaction)

        val fluid = this.fluid
        if (fluid.isEmpty) {
            return 0L
        }

        val toOutput = amount.coerceAtMost(fluid.amount)
        if (toOutput <= 0L) {
            return 0L
        }

        fluid.amount -= toOutput
        this.fluid = if (!fluid.isEmpty) fluid else FluidStack.empty()

        return toOutput.toLong()
    }

    override val canAccept get() = true
    override val canOutput get() = true

    override fun canOutput(resource: FluidResource) = this.container.canTake(this.slot, resource.toStack())

    override fun canAccept(resource: FluidResource) = this.container.canPlace(this.slot, resource.toStack())
}