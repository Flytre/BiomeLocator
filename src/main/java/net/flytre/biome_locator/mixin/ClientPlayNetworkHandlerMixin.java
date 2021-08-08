package net.flytre.biome_locator.mixin;


import net.flytre.biome_locator.client.ClientDataStorage;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method="onGameJoin", at = @At("HEAD"))
    public void biome_locator$clearStorage(GameJoinS2CPacket packet, CallbackInfo ci) {
        ClientDataStorage.initialized = false;
        ClientDataStorage.mobs = new HashMap<>();
        ClientDataStorage.blocks = new HashMap<>();
    }

}
