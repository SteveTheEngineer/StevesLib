package me.ste.library.mixin.common;

import me.ste.library.internal.ConnectionMixinExtension;
import me.ste.library.network.StevesLibConnection;
import me.ste.library.network.StevesLibNetwork;
import me.ste.library.network.StevesLibNetworkEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin implements ConnectionMixinExtension {
    @Shadow protected abstract ConnectionProtocol getCurrentProtocol();

    @Shadow private boolean disconnectionHandled;
    @Unique
    private StevesLibConnection stevesLibConnection;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(PacketFlow packetFlow, CallbackInfo ci) {
        this.stevesLibConnection = new StevesLibConnection((Connection) (Object) this);
    }

    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    public void handleDisconnection(CallbackInfo ci) {
        if (this.disconnectionHandled) {
            return;
        }

        StevesLibNetworkEvent.INSTANCE.getCONNECTION_END().invoker().end(this.stevesLibConnection);
    }

    @NotNull
    @Override
    public StevesLibConnection getSteveslib_connection() {
        return this.stevesLibConnection;
    }

    @NotNull
    @Override
    public ConnectionProtocol getSteveslib_protocol() {
        return this.getCurrentProtocol();
    }
}
