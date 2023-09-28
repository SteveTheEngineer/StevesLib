package me.ste.library.fabric.mixin.common;

import me.ste.library.network.StevesLibNetwork;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkAddon.class)
public class ServerLoginNetworkAddonMixin {
    @Inject(method = "registerOutgoingPacket", at = @At("HEAD"), remap = false, cancellable = true)
    public void registerOutgoingPacket(ClientboundCustomQueryPacket packet, CallbackInfo ci) {
        if (packet.getTransactionId() != StevesLibNetwork.INSTANCE.getTRANSACTION_ID() || !packet.getIdentifier().equals(StevesLibNetwork.INSTANCE.getCHANNEL_ID())) {
            return;
        }

        ci.cancel();
    }
}
