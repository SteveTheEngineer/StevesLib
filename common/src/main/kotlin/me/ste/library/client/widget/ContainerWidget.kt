package me.ste.library.client.widget

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

abstract class ContainerWidget(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int
) : AbstractContainerEventHandler(), NarratableEntry, Renderable {
    private var firstRender = true
    protected var isHovered = false

    // Utility
    protected val minecraft get() = Minecraft.getInstance()

    // Own logic
    protected val renderables = mutableListOf<Renderable>()
    protected val narratableEntries = mutableListOf<NarratableEntry>()
    protected val guiEventListeners = mutableListOf<GuiEventListener>()

    protected fun <T> addWidget(widget: T) where T : Renderable, T : NarratableEntry, T : GuiEventListener {
        this.renderables += widget
        this.narratableEntries += widget
        this.guiEventListeners += widget
    }

    protected fun reinit() {
        this.renderables.clear()
        this.narratableEntries.clear()
        this.guiEventListeners.clear()

        this.focused = null

        this.init()
    }

    fun resize(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height

        this.reinit()
    }

    protected abstract fun init()

    // ContainerEventHandler
    override fun children() = this.guiEventListeners

    override fun isMouseOver(mouseX: Double, mouseY: Double) = mouseX >= this.x && mouseY >= y && mouseX < x + width && mouseY < y + height

    // NarratableEntry
    private var lastNarratable: NarratableEntry? = null

    override fun updateNarration(output: NarrationElementOutput) {
        val list = this.narratableEntries.filter { it.isActive }
        val result = Screen.findNarratableWidget(list, this.lastNarratable)
            ?: return

        if (result.priority.isTerminal) {
            this.lastNarratable = result.entry
        }

        if (list.size > 1) {
            output.add(
                NarratedElementType.POSITION,
                Component.translatable("narrator.position.screen", result.index + 1, list.size)
            )

            if (result.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                output.add(
                    NarratedElementType.USAGE,
                    Component.translatable("narration.component_list.usage")
                )
            }
        }

        result.entry.updateNarration(output.nest())
    }

    override fun narrationPriority(): NarratableEntry.NarrationPriority {
        if (this.isFocused) {
            return NarratableEntry.NarrationPriority.FOCUSED
        }

        if (this.isHovered) {
            return NarratableEntry.NarrationPriority.HOVERED
        }

        return NarratableEntry.NarrationPriority.NONE
    }

    // Widget
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (this.firstRender) {
            this.reinit()
            this.firstRender = false
        }

        this.isHovered = this.isMouseOver(mouseX.toDouble(), mouseY.toDouble())

        for (widget in this.renderables) {
            widget.render(graphics, mouseX, mouseY, partialTick)
        }
    }
}