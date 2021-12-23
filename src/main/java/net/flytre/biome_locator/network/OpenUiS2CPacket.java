package net.flytre.biome_locator.network;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class OpenUiS2CPacket implements Packet<ClientPlayPacketListener> {

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        PacketHelper.applyUiPacket();
    }
}
