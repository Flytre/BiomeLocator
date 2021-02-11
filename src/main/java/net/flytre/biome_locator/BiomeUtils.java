package net.flytre.biome_locator;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class BiomeUtils {

    /*
    TODO: REQUIRE A WORLD FOR ALL OPERATIONS
     */

    private static final Map<String, String> modNameCache = Maps.newHashMap();


    public static Biome getBiome(Identifier id, World world) {
        return world.getRegistryManager().get(Registry.BIOME_KEY).get(id);

    }

    public static Identifier getId(Biome biome, World world) {
        return getRegistryKey(world).getId(biome);
    }

    public static Identifier getWorldId(Biome biome, World world) {
        return world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
    }


    @Environment(EnvType.CLIENT)
    public static String getBiomeName(Identifier biome) {
        return I18n.translate(Util.createTranslationKey("biome", biome));
    }

    @Environment(EnvType.CLIENT)
    public static String getBiomeName(Biome biome, World world) {
        return I18n.translate(Util.createTranslationKey("biome", getId(biome, world)));
    }

    public static String getMod(Biome biome, World world) {
        Identifier id = getId(biome, world);
        return getModFromModId(id.getNamespace());
    }


    public static String getModFromModId(String id) {
        if (id == null)
            return "";
        String any = modNameCache.getOrDefault(id, null);
        if (any != null)
            return any;
        String s = FabricLoader.getInstance().getModContainer(id).map(ModContainer::getMetadata).map(ModMetadata::getName).orElse(id);
        modNameCache.put(id, s);
        return s;
    }


    public static MutableRegistry<Biome> getRegistryKey(World world) {
        return world.getRegistryManager().get(Registry.BIOME_KEY);
    }

    public static List<Biome> getAllBiomes(World world) {
        return getRegistryKey(world).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }



    private static String translate(String type, Identifier id) {
        return I18n.translate(Util.createTranslationKey(type, id));
    }
}
