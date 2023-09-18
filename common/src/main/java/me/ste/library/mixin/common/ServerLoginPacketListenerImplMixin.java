package me.ste.library.mixin.common;

import me.ste.library.internal.network2.ConnectionStatus;
import me.ste.library.internal.network2.StevesLibConnection;
import me.ste.library.network2.StevesLibNetworkEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLoginPacketListenerImpl.class, priority = -2000)
public class ServerLoginPacketListenerImplMixin {
    @Shadow @Final public Connection connection;

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    public void handleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        var connection = StevesLibConnection.Companion.get(this.connection);

        if (connection.getStatus() != ConnectionStatus.READY) {
            return;
        }

        if (connection.getLoginTransactionId() != packet.getTransactionId()) {
            return;
        }

        ci.cancel();

        var data = packet.getData();

        if (data == null) {
            return;
        }

        connection.handleRawData(data);
    }

    @Inject(method = "handleAcceptedLogin", at = @At("HEAD"), cancellable = true)
    public void handleAcceptedLogin(CallbackInfo ci) {
        StevesLibNetworkEvent.INSTANCE.getLOGIN_READY_TO_ACCEPT().invoker().process((ServerLoginPacketListenerImpl) (Object) this, ci::cancel);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"), cancellable = true)
    public void tickTimeout(CallbackInfo ci) {
        StevesLibNetworkEvent.INSTANCE.getLOGIN_TOOK_TOO_LONG().invoker().process((ServerLoginPacketListenerImpl) (Object) this, ci::cancel);
    }
}
