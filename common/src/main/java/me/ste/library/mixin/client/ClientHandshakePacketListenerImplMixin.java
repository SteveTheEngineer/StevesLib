package me.ste.library.mixin.client;

import me.ste.library.internal.network2.ConnectionStatus;
import me.ste.library.internal.network2.StevesLibConnection;
import me.ste.library.internal.network2.StevesLibNetworkInternals;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientHandshakePacketListenerImpl.class, priority = -2000)
public class ClientHandshakePacketListenerImplMixin {
    @Shadow @Final private Connection connection;

    @Inject(method = "handleCustomQuery", at = @At("HEAD"), cancellable = true)
    public void handleCustomQuery(ClientboundCustomQueryPacket packet, CallbackInfo ci) {
        var connection = StevesLibConnection.Companion.get(this.connection);

        if (!packet.getIdentifier().equals(StevesLibNetworkInternals.INSTANCE.getCHANNEL_ID())) {
            return;
        }

        if (connection.getStatus() != ConnectionStatus.READY) {
            if (connection.getStatus() == ConnectionStatus.NEGOTIATING_RESERVATION) {
                connection.setLoginTransactionId(packet.getTransactionId());
            }

            return;
        }

        if (connection.getLoginTransactionId() != packet.getTransactionId()) {
            return;
        }

        ci.cancel();
        connection.handleRawData(packet.getData());
    }
}
