package me.ste.library.mixin.common;

import me.ste.library.network.StevesLibConnection;
import me.ste.library.network.StevesLibNetwork;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = -2000)
public class ServerGamePacketListenerImplMixin {
    @Shadow @Final public Connection connection;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    public void handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (!packet.getIdentifier().equals(StevesLibNetwork.INSTANCE.getCHANNEL_ID())) {
            return;
        }

        ci.cancel();

        var connection = StevesLibConnection.Companion.get(this.connection);
        connection.handleRawData(packet.getData());
    }
}
