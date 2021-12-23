package net.flytre.biome_locator.network;

import net.flytre.biome_locator.client.ClientDataStorage;
import net.flytre.biome_locator.client.screen.BiomeSelectionScreen;
import net.flytre.biome_locator.client.screen.BiomeStatDisplay;
import net.flytre.biome_locator.common.BiomeUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class PacketHelper {

    public static void applyUiPacket() {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        client.execute(() -> {
            if (player != null && player.isSneaking()) {
                BiomeSelectionScreen selectionScreen = new BiomeSelectionScreen(player.world);
                Biome biome = client.world.getBiome(player.getBlockPos());
                Identifier id = BiomeUtils.getId(biome, player.world);
                Biome exact = BiomeUtils.getBiome(id, player.world);
                client.setScreen(new BiomeStatDisplay(selectionScreen, exact));
            } else {
                client.setScreen(new BiomeSelectionScreen(player.world));
            }
            ClientDataStorage.requestDataIfNeeded();
        });
    }
}
