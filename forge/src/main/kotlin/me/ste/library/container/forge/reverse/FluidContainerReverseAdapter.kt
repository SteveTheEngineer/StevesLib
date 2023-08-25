package me.ste.library.container.forge.reverse

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge
import me.ste.library.container.resource.ResourceContainer
import me.ste.library.container.resource.ResourceHolder
import me.ste.library.resource.FluidResource
import me.ste.library.transaction.TransactionResult
import me.ste.library.transaction.TransactionShard
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction
import kotlin.math.absoluteValue

open class FluidContainerReverseAdapter(
    val storage: IFluidHandler
) : ResourceContainer<FluidResource> {
    private var change = 0
    private var changeFluid = FluidResource.EMPTY

    private val snapshots = mutableMapOf<Int, Pair<FluidResource, Int>>()

    private fun track(transaction: TransactionShard) {
        if (transaction.depth in this.snapshots) {
            return
        }

        transaction.onEnd {
            if (it == TransactionResult.REVERT) {
                val (changeFluid, change) = this.snapshots[transaction.depth]!!

                this.change = change
                this.changeFluid = changeFluid
            }

            this.snapshots -= transaction.depth
        }

        if (this.snapshots.isEmpty()) {
            transaction.onFinalEnd {
                if (it != TransactionResult.KEEP) {
                    this.change = 0
                    this.changeFluid = FluidResource.EMPTY

                    return@onFinalEnd
                }

                this.doChange(FluidAction.EXECUTE)

                this.change = 0
                this.changeFluid = FluidResource.EMPTY
            }
        }

        this.snapshots[transaction.depth] = this.changeFluid to this.change
    }

    private fun doChange(action: FluidAction) =
        if (this.change > 0) {
            this.storage.fill(FluidStackHooksForge.toForge(this.changeFluid.toStack(this.change.toLong())), action)
        } else if (this.change < 0) {
            -this.storage.drain(FluidStackHooksForge.toForge(this.changeFluid.toStack(-this.change.toLong())), action).amount
        } else {
            0
        }

    private fun exchange(fluid: FluidResource, amount: Int, transaction: TransactionShard): Int {
        this.track(transaction)

        if (amount > 0 && !(this.change == 0 || this.changeFluid.isSame(fluid))) {
            return 0
        }

        this.changeFluid = fluid

        val oldChange = this.change
        this.change += amount
        val newChange = this.doChange(FluidAction.SIMULATE)
        this.change = newChange

        return (newChange - oldChange).absoluteValue
    }

    override val slots get() = this.storage.tanks

    override fun getSlot(slot: Int) = FluidHolderReverseAdapter(this, slot, 0)

    override val canAccept get() = this.storage.tanks > 0

    override val canOutput get() = this.storage.tanks > 0

    override fun canOutput(resource: FluidResource) = this.canOutput

    override fun canAccept(resource: FluidResource): Boolean {
        val size = this.storage.tanks

        if (size <= 0) {
            return false
        }

        val stack = FluidStackHooksForge.toForge(resource.toStack())

        for (slot in 0 until size) {
            if (!this.storage.isFluidValid(slot, stack)) {
                return false
            }
        }

        return true
    }

    override fun accept(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        return this.exchange(resource, amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), transaction).toLong()
    }

    override fun output(resource: FluidResource, amount: Long, transaction: TransactionShard): Long {
        return this.exchange(resource, -(amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()), transaction).toLong()
    }

    override fun getResource(resource: FluidResource) = null

    override fun iterator(): Iterator<ResourceHolder<FluidResource>> {
        var change = this.change
        var slot = 0

        return object : Iterator<ResourceHolder<FluidResource>> {
            override fun hasNext() = slot < this@FluidContainerReverseAdapter.storage.tanks

            override fun next(): FluidHolderReverseAdapter {
                val currentSlot = slot++

                val fluid = this@FluidContainerReverseAdapter.storage.getFluidInTank(currentSlot)
                val capacity = this@FluidContainerReverseAdapter.storage.getTankCapacity(currentSlot)

                val resource = FluidResource(FluidStackHooksForge.fromForge(fluid))

                var newAmount = fluid.amount

                if (this@FluidContainerReverseAdapter.changeFluid == resource) {
                    newAmount += change

                    if (newAmount < 0) {
                        change = newAmount
                        newAmount = 0
                    } else if (newAmount > capacity) {
                        change = newAmount - capacity
                        newAmount = capacity
                    } else {
                        change = 0
                    }
                }

                return FluidHolderReverseAdapter(this@FluidContainerReverseAdapter, currentSlot, newAmount - fluid.amount)
            }
        }
    }

}