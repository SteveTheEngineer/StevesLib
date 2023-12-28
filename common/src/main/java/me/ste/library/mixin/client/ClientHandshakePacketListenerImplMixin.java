package me.ste.library.mixin.client;

import me.ste.library.network.ConnectionStatus;
import me.ste.library.network.StevesLibConnection;
import me.ste.library.network.StevesLibNetwork;
import me.ste.library.network.StevesLibNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientHandshakePacketListenerImpl.class, priority = -2000)
public class ClientHandshakePacketListenerImplMixin {
    @Shadow @Final private Connection connection;

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleCustomQuery", at = @At("HEAD"), cancellable = true)
    public void handleCustomQuery(ClientboundCustomQueryPacket packet, CallbackInfo ci) {
        if (packet.getTransactionId() != StevesLibNetwork.INSTANCE.getTRANSACTION_ID() || !packet.getIdentifier().equals(StevesLibNetwork.INSTANCE.getCHANNEL_ID())) {
            return;
        }

        ci.cancel();

        var data = packet.getData();
        var connection = StevesLibConnection.Companion.get(this.connection);
        connection.handleRawData(data);
    }

    @Inject(method = "handleGameProfile", at = @At("HEAD"))
    public void handleGameProfile(ClientboundGameProfilePacket packet, CallbackInfo ci) {
        var connection = StevesLibConnection.Companion.get(this.connection);

        if (!connection.getStatus().isFinal()) {
            connection.setStatus(ConnectionStatus.UNSUPPORTED);
            StevesLibNetworkEvent.Companion.getCONNECTION_FINAL_STATUS().invoker().finalStatus(connection);
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void onDisconnect(Component reason, CallbackInfo ci) {
        var server = this.minecraft.getSingleplayerServer();

        if (server == null) {
            return;
        }

        ((MinecraftAccessor) this.minecraft).invokeUpdateScreenAndTick(new ProgressScreen(true));

        while (!server.isShutdown()) {
            ((MinecraftAccessor) this.minecraft).invokeRunTick(false);
        }
    }
}
