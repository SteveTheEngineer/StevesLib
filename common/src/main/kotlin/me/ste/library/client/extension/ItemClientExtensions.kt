package me.ste.library.client.extension

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

interface ItemClientExtensions {
    fun hasDynamicRendering(): Boolean = false
    fun renderDynamicItem(stack: ItemStack, transformType: ItemTransforms.TransformType, poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {}
    fun getArmPose(entity: LivingEntity, hand: InteractionHand, stack: ItemStack): HumanoidModel.ArmPose? = null
}