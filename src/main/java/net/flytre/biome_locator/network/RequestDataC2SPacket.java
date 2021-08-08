package net.flytre.biome_locator.network;

import net.flytre.biome_locator.server.ServerBiomeDataProcessor;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class RequestDataC2SPacket implements Packet<ServerPlayPacketListener> {

    @Override
    public void write(PacketByteBuf buf) {

    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ServerPlayNetworkHandler handler = ((ServerPlayNetworkHandler) listener);
        ServerBiomeDataProcessor.sendDataToClient(handler.getPlayer(), handler.getPlayer().world);
    }
}
