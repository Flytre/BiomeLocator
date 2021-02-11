package net.flytre.biome_locator;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GiantBiomePacket {

    public static final Identifier PACKET_ID = new Identifier("biome_locator", "biome_dump");
    private final HashMap<Identifier, Set<String>> mobs;
    private final HashMap<Identifier, Set<String>> blocks;

    public GiantBiomePacket(HashMap<Identifier, Set<String>> mobs, HashMap<Identifier, Set<String>> blocks) {
        this.mobs = mobs;
        this.blocks = blocks;
    }

    public static GiantBiomePacket fromPacket(PacketByteBuf packet) {
        HashMap<Identifier, Set<String>> mobs = new HashMap<>();
        HashMap<Identifier, Set<String>> blocks = new HashMap<>();

        for (HashMap<Identifier, Set<String>> hashMap : Arrays.asList(mobs, blocks)) {
            int hashSize = packet.readInt();
            for (int i = 0; i < hashSize; i++) {
                Identifier id = packet.readIdentifier();
                int setSize = packet.readInt();
                Set<String> strings = new HashSet<>();
                for (int k = 0; k < setSize; k++) {
                    strings.add(packet.readString());
                }
                hashMap.put(id, strings);
            }
        }
        return new GiantBiomePacket(mobs, blocks);
    }

    public PacketByteBuf toBuf() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        for (HashMap<Identifier, Set<String>> hashMap : Arrays.asList(mobs, blocks)) {
            packet.writeInt(hashMap.keySet().size());
            for (Identifier identifier : hashMap.keySet()) {
                packet.writeIdentifier(identifier);
                packet.writeInt(hashMap.get(identifier).size());
                for (String string : hashMap.get(identifier)) {
                    packet.writeString(string);
                }
            }
        }
        return packet;
    }

    public HashMap<Identifier, Set<String>> getMobs() {
        return mobs;
    }

    public HashMap<Identifier, Set<String>> getBlocks() {
        return blocks;
    }
}
