package me.ste.library.client.extension.forge

import com.mojang.blaze3d.vertex.PoseStack
import me.ste.library.client.extension.ItemClientExtensions
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class ItemClientExtensionsAdapter(
    private val extensions: ItemClientExtensions
) : IClientItemExtensions {
    private val renderer =
        if (extensions.hasDynamicRendering()) {
            object : BlockEntityWithoutLevelRenderer(null, null) {
                override fun reload(
                    preparationBarrier: PreparableReloadListener.PreparationBarrier,
                    resourceManager: ResourceManager,
                    preparationsProfiler: ProfilerFiller,
                    reloadProfiler: ProfilerFiller,
                    backgroundExecutor: Executor,
                    gameExecutor: Executor
                ) = CompletableFuture.completedFuture(null as Void?)

                override fun onResourceManagerReload(resourceManager: ResourceManager) {}

                override fun renderByItem(
                    stack: ItemStack,
                    context: ItemDisplayContext,
                    poseStack: PoseStack,
                    bufferSource: MultiBufferSource,
                    packedLight: Int,
                    packedOverlay: Int
                ) {
                    extensions.renderDynamicItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay)
                }
            }
        } else {
            null
        }

    override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer {
        if (this.renderer == null) {
            return super.getCustomRenderer()
        }

        return this.renderer
    }

    override fun getArmPose(
        entity: LivingEntity,
        hand: InteractionHand,
        stack: ItemStack
    ) = this.extensions.getArmPose(entity, hand, stack)
}