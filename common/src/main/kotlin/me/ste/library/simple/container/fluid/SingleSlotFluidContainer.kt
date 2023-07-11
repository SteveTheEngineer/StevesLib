package me.ste.library.simple.container.fluid

import me.ste.library.transfer.fluid.SnapshotFluidContainer
import me.ste.library.transfer.resource.ResourceWithAmount
import me.ste.library.transfer.resource.StackableFluid
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.level.material.Fluid

open class SingleSlotFluidContainer(
    open val capacity: Long,

    open val maxAccept: Long,
    open val maxOutput: Long,

    protected val setChanged: Runnable = Runnable {}
) : SnapshotFluidContainer<ResourceWithAmount<StackableFluid>> {
    // Internal
    private var resourceWithAmountCache = ResourceWithAmount.EMPTY_FLUID
    private var hasChanged = true
    private var changesUnsaved = false

    private fun removeFluid(amount: Long, saveImmediately: Boolean = false) {
        this.amount -= amount

        if (this.amount <= 0L) {
            this.fluid = StackableFluid.EMPTY
            this.amount = 0L
        }

        this.setChanged(saveImmediately)
    }

    private fun addFluid(amount: Long, saveImmediately: Boolean = false) {
        this.amount += amount

        if (this.amount > this.capacity) {
            this.amount = this.capacity
        }

        this.setChanged(saveImmediately)
    }

    private fun setChanged(saveImmediately: Boolean = false) {
        this.hasChanged = true

        if (saveImmediately) {
            this.setChanged.run()
            this.changesUnsaved = false
        } else {
            this.changesUnsaved = true
        }
    }

    // Public read-only values
    var fluid = StackableFluid.EMPTY
        protected set

    var amount = 0L
        protected set

    val asResource: ResourceWithAmount<StackableFluid> get() {
        if (this.hasChanged) {
            this.resourceWithAmountCache = ResourceWithAmount(this.fluid, this.amount)
            this.hasChanged = false
        }

        return this.resourceWithAmountCache
    }

    // Utility
    val isEmpty get() = this.amount <= 0L

    fun isCompatibleWith(fluid: StackableFluid) = this.fluid.isEmpty || this.fluid == fluid

    fun hasFluid(fluid: StackableFluid) = !this.fluid.isEmpty && this.fluid == fluid

    fun hasFluid(amount: Long) = this.amount >= amount

    fun hasFluid(fluid: StackableFluid, amount: Long) = this.hasFluid(fluid) && this.hasFluid(amount)

    fun useFluid(amount: Long): Boolean {
        if (this.amount < amount) {
            return false
        }

        this.removeFluid(amount, true)

        return true
    }

    fun removeFluid(amount: Long) {
        this.removeFluid(amount, true)
    }

    fun addFluid(amount: Long) {
        this.addFluid(amount, true)
    }

    fun setFluid(fluid: StackableFluid, amount: Long) {
        this.fluid = fluid
        this.amount = amount
        this.setChanged(true)
    }

    fun setFluid(amount: Long) {
        this.amount = amount
        this.setChanged(true)
    }

    // Overridable
    open fun canAccept(side: Direction?, resource: StackableFluid) = this.maxAccept > 0L

    open fun canOutput(side: Direction?, resource: StackableFluid) = this.maxOutput > 0L

    // Implementation
    override fun getResource(side: Direction?, slot: Int): ResourceWithAmount<StackableFluid> {
        if (slot != 0) {
            return ResourceWithAmount.EMPTY_FLUID
        }

        return this.asResource
    }

    override fun getCapacity(side: Direction?, slot: Int): Long {
        if (slot != 0) {
            return 0L
        }

        return this.capacity
    }

    override fun accept(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long {
        if (!this.canAccept(side, 0, resource.resource)) {
            return 0L
        }

        if (!this.isCompatibleWith(resource.resource)) {
            return 0L
        }

        val toAccept = resource.amount.coerceAtMost(this.maxAccept).coerceAtMost(this.capacity - this.amount)

        this.fluid = resource.resource
        this.addFluid(toAccept)

        return toAccept
    }

    override fun output(side: Direction?, resource: ResourceWithAmount<StackableFluid>): Long {
        if (!this.canOutput(side, resource.resource)) {
            return 0L
        }

        if (this.hasFluid(this.fluid)) {
            return 0L
        }

        val toOutput = resource.amount.coerceAtMost(this.maxOutput).coerceAtMost(this.amount)

        this.removeFluid(toOutput)

        return toOutput
    }

    override fun getContainerSize(side: Direction?) = 1

    override fun canAccept(side: Direction?, slot: Int, resource: StackableFluid) = slot == 0 && this.canAccept(side, resource)

    override fun canAccept(side: Direction?) = true

    override fun canOutput(side: Direction?) = true

    override fun createSnapshot(side: Direction?) = this.asResource.copy()

    override fun readSnapshot(side: Direction?, snapshot: ResourceWithAmount<StackableFluid>) {
        this.fluid = snapshot.resource
        this.amount = snapshot.amount
    }

    override fun saveChanges(side: Direction?) {
        if (!this.changesUnsaved) {
            return
        }

        this.setChanged.run()
        this.changesUnsaved = false
    }

    fun save(tag: CompoundTag) {
        val childTag = CompoundTag()

        if (!this.fluid.isEmpty) {
            this.fluid.save(childTag)
            childTag.putLong("Amount", this.amount)
        }

        tag.put("Fluid", childTag)
    }

    fun load(tag: CompoundTag) {
        if (!tag.contains("Fluid", Tag.TAG_COMPOUND.toInt())) {
            this.fluid = StackableFluid.EMPTY
            this.amount = 0
            return
        }

        val childTag = tag.getCompound("Fluid")

        this.fluid = StackableFluid.load(childTag)
        this.amount = childTag.getLong("Amount")
    }
}