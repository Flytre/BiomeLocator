package net.flytre.biome_locator.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.biome_locator.common.BiomeUtils;
import net.flytre.biome_locator.network.RequestDataC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ClientDataStorage {
    public static Map<Identifier, Set<String>> mobs = new HashMap<>();
    public static Map<Identifier, Set<String>> blocks = new HashMap<>();
    public static boolean initialized = false;

    public static boolean hasResource(String string, Biome biome, World world) {
        if (mobs.size() == 0 && blocks.size() == 0)
            return false;
        Set<String> resources = new HashSet<>();
        Identifier id = BiomeUtils.getId(biome, world);
        if (id == null || !mobs.containsKey(id) || !blocks.containsKey(id))
            return false;
        resources.addAll(mobs.get(id));
        resources.addAll(blocks.get(id));
        for (String str : resources) {
            if (str.toLowerCase().contains(string))
                return true;
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    public static void requestDataIfNeeded() {
        if (!initialized || mobs == null || blocks == null)
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new RequestDataC2SPacket());
        initialized = true;
    }

}
