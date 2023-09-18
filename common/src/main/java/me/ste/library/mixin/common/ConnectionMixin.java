package me.ste.library.mixin.common;

import me.ste.library.internal.network2.ConnectionMixinExtension;
import me.ste.library.internal.network2.StevesLibConnection;
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

    @Unique
    private StevesLibConnection stevesLibConnection;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(PacketFlow packetFlow, CallbackInfo ci) {
        this.stevesLibConnection = new StevesLibConnection((Connection) (Object) this);
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
