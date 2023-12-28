package me.ste.library.mixin.common;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import me.ste.library.StevesLib;
import me.ste.library.network.ConnectionStatus;
import me.ste.library.network.StevesLibConnection;
import me.ste.library.network.StevesLibNetwork;
import me.ste.library.network.StevesLibNetworkEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLoginPacketListenerImpl.class, priority = -2000)
public class ServerLoginPacketListenerImplMixin {
    @Shadow @Final public Connection connection;

    @Shadow @Nullable private GameProfile gameProfile;

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"), cancellable = true)
    public void handleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        if (packet.getTransactionId() != StevesLibNetwork.INSTANCE.getTRANSACTION_ID()) {
            return;
        }

        ci.cancel();

        var data = packet.getData();
        var connection = StevesLibConnection.Companion.get(this.connection);
        connection.handleRawData(data);
    }

    @Inject(method = "onDisconnect", at = @At("TAIL"))
    public void onDisconnect(Component message, CallbackInfo callbackInfo) {
        if (this.gameProfile != null && this.server.isSingleplayerOwner(this.gameProfile)) {
            StevesLib.INSTANCE.getLOGGER().info("Shutting down the integrated server due to a disconnect during the login stage.");
            this.server.halt(false);
        }
    }

    @Inject(method = "handleAcceptedLogin", at = @At("HEAD"), cancellable = true)
    public void handleAcceptedLogin(CallbackInfo ci) {
        var connection = StevesLibConnection.Companion.get(this.connection);

        if (connection.getStatus() == ConnectionStatus.NONE) {
            connection.startNegotiation();
        }

        if (!connection.getStatus().isFinal()) {
            ci.cancel();
            return;
        }

        var result = StevesLibNetworkEvent.Companion.getLOGIN_READY_TO_ACCEPT().invoker().process((ServerLoginPacketListenerImpl) (Object) this);

        if (result.isFalse()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"), cancellable = true)
    public void tickTimeout(CallbackInfo ci) {
        var result = StevesLibNetworkEvent.Companion.getLOGIN_TIMEOUT().invoker().process((ServerLoginPacketListenerImpl) (Object) this);

        if (result.isFalse()) {
            ci.cancel();
        }
    }
}
