package me.ste.library.transfer.forge.adapter

import me.ste.library.forge.ResourceConversionsForge
import me.ste.library.util.SnapshotUtils
import me.ste.library.transfer.fluid.SnapshotFluidContainer
import net.minecraft.core.Direction
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction
import kotlin.math.min

open class FluidContainerForgeAdapter(
    private val container: SnapshotFluidContainer<*>,
    private val side: Direction?
) : IFluidHandler {
    override fun getTanks() = this.container.getContainerSize(this.side)

    override fun getFluidInTank(slot: Int) = ResourceConversionsForge.toForgeWithAmount(
        this.container.getResource(this.side, slot)
    )

    override fun getTankCapacity(slot: Int) = min(
        this.container.getCapacity(this.side, slot),
        Int.MAX_VALUE.toLong()
    ).toInt()

    override fun isFluidValid(slot: Int, fluid: FluidStack) =
        this.container.canAccept(this.side, slot, ResourceConversionsForge.fromForge(fluid))

    override fun fill(fluid: FluidStack, action: FluidAction): Int {
        val resource = ResourceConversionsForge.fromForgeWithAmount(fluid)

        val accepted = SnapshotUtils.simulate(this.container, this.side, action == FluidAction.SIMULATE) {
            it.accept(this.side, resource)
        }

        if (accepted > resource.amount) {
            throw IllegalStateException("Accepted resource amount is greater than pushed.")
        }

        return accepted.toInt()
    }

    override fun drain(fluid: FluidStack, action: FluidAction): FluidStack {
        val resource = ResourceConversionsForge.fromForgeWithAmount(fluid)

        val output = SnapshotUtils.simulate(this.container, this.side, action == FluidAction.SIMULATE) {
            it.output(this.side, resource)
        }

        if (output > resource.amount) {
            throw IllegalStateException("Output resource amount is greater than pushed.")
        }

        val result = fluid.copy()
        result.amount = output.toInt()
        return result
    }

    override fun drain(amount: Int, action: FluidAction): FluidStack {
        val fluid = (0 until this.container.getContainerSize(this.side))
            .map { this.container.getResource(this.side, it) }
            .find { it.amount > 0 && !it.resource.isEmpty }
            ?: return FluidStack.EMPTY

        val toOutput = fluid.copy(amount = amount.toLong())

        val output = SnapshotUtils.simulate(this.container, this.side, action == FluidAction.SIMULATE) {
            it.output(this.side, toOutput)
        }

        if (output > amount) {
            throw IllegalStateException("Output resource amount is greater than pushed.")
        }

        val result = ResourceConversionsForge.toForgeWithAmount(toOutput)
        result.amount = output.toInt()
        return result
    }
}