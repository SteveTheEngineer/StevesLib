package me.ste.library.mixin.client;

import me.ste.library.network.StevesLibConnection;
import me.ste.library.network.StevesLibNetwork;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPacketListener.class, priority = -2000)
public class ClientPacketListenerMixin {
    @Shadow @Final private Connection connection;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    public void handleCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (!packet.getIdentifier().equals(StevesLibNetwork.INSTANCE.getCHANNEL_ID())) {
            return;
        }

        ci.cancel();

        var connection = StevesLibConnection.Companion.get(this.connection);
        connection.handleRawData(packet.getData());
    }
}
