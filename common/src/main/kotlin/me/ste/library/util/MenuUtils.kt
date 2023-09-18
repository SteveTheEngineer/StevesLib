package me.ste.library.util

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import java.util.function.Consumer

object MenuUtils {
    fun addPlayerInventorySlots(addSlot: Consumer<in Slot>, playerInv: Inventory, startX: Int, startY: Int) {
        for (y in 0..2) {
            for (x in 0..8) {
                addSlot.accept(Slot(playerInv, x + y * 9 + 9, startX + x * 18, startY + y * 18))
            }
        }

        for (x in 0..8) {
            addSlot.accept(Slot(playerInv, x, startX + x * 18, startY + 58))
        }
    }
}