package net.flytre.biome_locator;

import io.netty.buffer.Unpooled;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.*;

public class GiantBiomePacket {

    public static final Identifier PACKET_ID = new Identifier("biome_locator", "biome_dump");
    private final Map<Identifier, Set<String>> mobs;
    private final Map<Identifier, Set<String>> blocks;

    public GiantBiomePacket(Map<Identifier, Set<String>> mobs, Map<Identifier, Set<String>> blocks) {
        this.mobs = mobs;
        this.blocks = blocks;
    }

    public static GiantBiomePacket fromPacket(PacketByteBuf packet) {
        Map<Identifier, Set<String>> mobs = PacketUtils.mapFromPacket(packet, PacketByteBuf::readIdentifier, (buf) -> PacketUtils.setFromPacket(buf, PacketByteBuf::readString));
        Map<Identifier, Set<String>> blocks = PacketUtils.mapFromPacket(packet, PacketByteBuf::readIdentifier, (buf) -> PacketUtils.setFromPacket(buf, PacketByteBuf::readString));
        return new GiantBiomePacket(mobs, blocks);
    }

    public PacketByteBuf toBuf() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        for (Map<Identifier, Set<String>> hashMap : Arrays.asList(mobs, blocks))
            PacketUtils.toPacket(packet, hashMap, (identifier, packetByteBuf) -> packetByteBuf.writeIdentifier(identifier), (set, buf) -> PacketUtils.toPacket(buf, set, (str, buf2) -> buf2.writeString(str)));
        return packet;
    }

    public Map<Identifier, Set<String>> getMobs() {
        return mobs;
    }

    public Map<Identifier, Set<String>> getBlocks() {
        return blocks;
    }
}
