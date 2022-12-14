package me.langyue.equipmentstandard.mixin.client;

import me.langyue.equipmentstandard.network.ClientPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin", at = @At(value = "TAIL"))
    private void onGameJoin(CallbackInfo ci) {
        ClientPacket.onReady();
    }
}
