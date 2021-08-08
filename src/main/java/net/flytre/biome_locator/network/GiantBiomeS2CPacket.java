package net.flytre.biome_locator.network;

import net.flytre.biome_locator.client.ClientDataStorage;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class GiantBiomeS2CPacket implements Packet<ClientPlayPacketListener> {

    private final Map<Identifier, Set<String>> mobs;
    private final Map<Identifier, Set<String>> blocks;

    public GiantBiomeS2CPacket(Map<Identifier, Set<String>> mobs, Map<Identifier, Set<String>> blocks) {
        this.mobs = mobs;
        this.blocks = blocks;
    }

    public GiantBiomeS2CPacket(PacketByteBuf packet) {
        mobs = PacketUtils.mapFromPacket(packet, PacketByteBuf::readIdentifier, (buf) -> PacketUtils.setFromPacket(buf, PacketByteBuf::readString));
        blocks = PacketUtils.mapFromPacket(packet, PacketByteBuf::readIdentifier, (buf) -> PacketUtils.setFromPacket(buf, PacketByteBuf::readString));
    }

    @Override
    public void write(PacketByteBuf packet) {
        for (Map<Identifier, Set<String>> hashMap : Arrays.asList(mobs, blocks))
            PacketUtils.toPacket(packet, hashMap, (identifier, packetByteBuf) -> packetByteBuf.writeIdentifier(identifier), (set, buf) -> PacketUtils.toPacket(buf, set, (str, buf2) -> buf2.writeString(str)));
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        ClientDataStorage.mobs = mobs;
        ClientDataStorage.blocks = blocks;
    }
}
