package net.flytre.biome_locator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.biome_locator.mixin.WeightedBlockStateProviderAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BiomeDataFetcher {

    public static final Random RANDOM = new Random();
    public static HashMap<Identifier, Set<String>> mobs = new HashMap<>();
    public static HashMap<Identifier, Set<String>> blocks = new HashMap<>();
    public static boolean cached = false;

    @Environment(EnvType.CLIENT)
    public static void requestCacheIfNeeded() {
        if (mobs.size() > 0 || blocks.size() > 0)
            return;
        ClientPlayNetworking.send(BiomeLocator.REQUEST_CACHE, PacketByteBufs.empty());
    }

    public static void receivedCacheRequest(ServerPlayerEntity player, World world) {
        cacheAll(world);
        GiantBiomePacket packet = new GiantBiomePacket(mobs, blocks);
        ServerPlayNetworking.send(player, GiantBiomePacket.PACKET_ID, packet.toBuf());
    }


    public static void cacheAll(World world) {
        if (!cached || (mobs.size() == 0 && blocks.size() == 0)) {
            cached = true;
            for (Biome biome : BiomeUtils.getAllBiomes(world)) {
                mobs.put(BiomeUtils.getId(biome, world), getMobSpawns(biome));
                blocks.put(BiomeUtils.getId(biome, world), getMiscResources(biome));
            }
        }
    }

    public static boolean hasResource(String string, Biome biome, World world) {
        if (mobs.size() == 0 && blocks.size() == 0)
            return false;
        Set<String> resources = new HashSet<>();
        Identifier id = BiomeUtils.getId(biome, world);
        if(id == null || !mobs.containsKey(id) || !blocks.containsKey(id))
            return false;
        resources.addAll(mobs.get(id));
        resources.addAll(blocks.get(id));
        for (String str : resources) {
            if (str.toLowerCase().contains(string))
                return true;
        }
        return false;
    }

    public static Set<String> getMobSpawns(Biome biome) {
        Set<String> result = new HashSet<>();
        SpawnSettings spawnSettings = biome.getSpawnSettings();
        for (SpawnGroup group : SpawnGroup.values()) {
            for (SpawnSettings.SpawnEntry spawnEntry : spawnSettings.getSpawnEntry(group)) {
                result.add(translateKey("entity", Registry.ENTITY_TYPE.getId(spawnEntry.type)));
            }
        }
        return result;
    }

    private static String translateKey(String type, Identifier id) {
        return Util.createTranslationKey(type, id);
    }

    private static Set<String> sampleProvider(BlockStateProvider provider) {
        Set<String> result = new HashSet<>();

        if (provider instanceof WeightedBlockStateProvider) {

            WeightedBlockStateProvider weighted = (WeightedBlockStateProvider) provider;
            WeightedList<BlockState> states = ((WeightedBlockStateProviderAccessor) weighted).getList();
            states.stream().forEach(i -> result.add(i.getBlock().getTranslationKey()));
            return result;
        }
        for (int i = 0; i < 100; i++) {
            BlockState state = provider.getBlockState(RANDOM, new BlockPos(RANDOM.nextInt(10000), RANDOM.nextInt(10000), RANDOM.nextInt(10000)));
            result.add(state.getBlock().getTranslationKey());
        }
        return result;
    }

    public static Set<String> getMiscResources(Biome biome) {
        final Set<String> result = new HashSet<>();

        for (List<Supplier<ConfiguredFeature<?, ?>>> list : biome.getGenerationSettings().getFeatures()) {
            for (Supplier<ConfiguredFeature<?, ?>> feature : list) {
                FeatureConfig config = getConfig(feature.get());
                result.addAll(getHardcodedResources(biome, getFeature(feature.get())));
                if (config != null) {
                    for (Field field : getFields(config)) {
                        try {
                            if (field.getType().isAssignableFrom(BlockStateProvider.class)) {
                                field.setAccessible(true);
                                BlockStateProvider provider = (BlockStateProvider) field.get(config);
                                result.addAll(sampleProvider(provider));

                            } else if (field.getType().isAssignableFrom(BlockState.class)) {
                                field.setAccessible(true);
                                BlockState state = (BlockState) field.get(config);
                                result.add(state.getBlock().getTranslationKey());
                            }

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
        return result;
    }

    private static @Nullable FeatureConfig getConfig(ConfiguredFeature<?, ?> feature) {
        while (feature.config instanceof DecoratedFeatureConfig) {
            feature = ((DecoratedFeatureConfig) feature.config).feature.get();
        }
        if (feature.config instanceof FeatureConfig) {
            return feature.config;
        }
        return null;
    }

    private static @Nullable Feature<?> getFeature(ConfiguredFeature<?, ?> feature) {
        while (feature.config instanceof DecoratedFeatureConfig) {
            feature = ((DecoratedFeatureConfig) feature.config).feature.get();
        }
        if (feature.feature instanceof Feature<?>) {
            return feature.feature;
        }
        return null;
    }

    private static <T> List<Field> getFields(T t) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = t.getClass();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }


    private static Set<String> getHardcodedResources(Biome biome, Feature<?> feature) {
        Set<Block> states = new HashSet<>();
        if (feature instanceof BambooFeature) {
            states.add(Blocks.BAMBOO);
            states.add(Blocks.PODZOL);

        }
        if (feature instanceof BasaltPillarFeature) {
            states.add(Blocks.BASALT);
        }
        if (feature instanceof BlueIceFeature)
            states.add(Blocks.BLUE_ICE);

        if (feature instanceof ChorusPlantFeature)
            states.add(Blocks.CHORUS_PLANT);
        if (feature instanceof CoralFeature)
            states.addAll(BlockTags.CORAL_BLOCKS.values());
        if (feature instanceof EndIslandFeature)
            states.add(Blocks.END_STONE);
        if (feature instanceof EndSpikeFeature)
            states.add(Blocks.OBSIDIAN);
        if (feature instanceof FreezeTopLayerFeature) {
            if (biome.getTemperature() <= 0.15) {
                states.add(Blocks.ICE);
                states.add(Blocks.SNOW);
            }
        }
        if (feature instanceof GlowstoneBlobFeature)
            states.add(Blocks.GLOWSTONE);
        if (feature instanceof IceSpikeFeature)
            states.add(Blocks.PACKED_ICE);
        if (feature instanceof KelpFeature) {
            states.add(Blocks.KELP);
            states.add(Blocks.KELP_PLANT);
        }
        if (feature instanceof SeagrassFeature) {
            states.add(Blocks.SEAGRASS);
            states.add(Blocks.TALL_SEAGRASS);
        }
        if (feature instanceof SeaPickleFeature)
            states.add(Blocks.SEA_PICKLE);

        if (feature instanceof VinesFeature)
            states.add(Blocks.VINE);
        if (feature instanceof WeepingVinesFeature) {
            states.add(Blocks.WEEPING_VINES);
            states.add(Blocks.NETHER_WART_BLOCK);
        }

        return states.stream().map(Block::getTranslationKey).collect(Collectors.toSet());
    }
}
