package net.flytre.biome_locator.network;

import net.flytre.biome_locator.common.BiomeLocator;
import net.flytre.biome_locator.common.BiomeLocatorItem;
import net.flytre.biome_locator.common.BiomeUtils;
import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class SearchBiomeC2SPacket implements Packet<ServerPlayPacketListener> {

    private final Identifier id;

    public SearchBiomeC2SPacket(Identifier id) {
        this.id = id;
    }

    public SearchBiomeC2SPacket(PacketByteBuf buf) {
        this.id = buf.readIdentifier();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ServerPlayNetworkHandler handler = ((ServerPlayNetworkHandler)listener);
        ServerPlayerEntity player = handler.getPlayer();
        Objects.requireNonNull(player.getServer()).execute(() -> {
            if (BiomeLocatorItem.hasRequiredAdvancement(player))
                BiomeLocatorItem.startThreadedSearch(InventoryUtils.getHoldingStack(player, i -> i.getItem() == BiomeLocator.BIOME_LOCATOR), player.world, player, BiomeUtils.getBiome(id, player.world));
        });
    }
}
