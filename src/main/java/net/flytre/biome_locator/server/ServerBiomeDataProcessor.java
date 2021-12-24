package net.flytre.biome_locator.server;

import net.flytre.biome_locator.common.BiomeUtils;
import net.flytre.biome_locator.mixin.PlacedFeatureAccessor;
import net.flytre.biome_locator.mixin.WeightedBlockStateProviderAccessor;
import net.flytre.biome_locator.network.GiantBiomeS2CPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServerBiomeDataProcessor {

    public static final Random RANDOM = new Random();
    private static Cache cache = null;


    public static void sendDataToClient(ServerPlayerEntity player, World world) {
        cacheAll(world);
        GiantBiomeS2CPacket infoPacket = new GiantBiomeS2CPacket(cache.mobs, cache.blocks);
        player.networkHandler.sendPacket(infoPacket);
    }

    private static void cacheAll(World world) {
        if (cache == null) {
            Map<Identifier, Set<String>> mobs = new HashMap<>(), blocks = new HashMap<>();
            for (Biome biome : BiomeUtils.getAllBiomes(world)) {
                mobs.put(BiomeUtils.getId(biome, world), getMobSpawns(biome));
                blocks.put(BiomeUtils.getId(biome, world), getMiscResources(biome, world));
            }
            cache = new Cache(mobs, blocks);
        }
    }

    public static Set<String> getMobSpawns(Biome biome) {
        Set<String> result = new HashSet<>();
        SpawnSettings spawnSettings = biome.getSpawnSettings();
        for (SpawnGroup group : SpawnGroup.values()) {
            for (SpawnSettings.SpawnEntry spawnEntry : spawnSettings.getSpawnEntries(group).getEntries()) {
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
            DataPool<BlockState> states = ((WeightedBlockStateProviderAccessor) weighted).getList();
            states.getEntries().forEach(i -> result.add(i.getData().getBlock().getTranslationKey()));
            return result;
        }
        for (int i = 0; i < 100; i++) {
            BlockState state = provider.getBlockState(RANDOM, new BlockPos(RANDOM.nextInt(10000), RANDOM.nextInt(10000), RANDOM.nextInt(10000)));
            result.add(state.getBlock().getTranslationKey());
        }
        return result;
    }

    public static Set<String> getMiscResources(Biome biome, World world) {
        final Set<String> result = new HashSet<>();

        for (List<Supplier<PlacedFeature>> list : biome.getGenerationSettings().getFeatures()) {
            for (Supplier<PlacedFeature> feature : list) {

                ConfiguredFeature<?, ?> configuredFeature = ((PlacedFeatureAccessor) feature.get()).getFeature().get();

                FeatureConfig config = configuredFeature.getConfig();
                result.addAll(getHardcodedResources(biome, configuredFeature.getFeature()));
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

        //add Geode stuff

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

    private record Cache(Map<Identifier, Set<String>> mobs, Map<Identifier, Set<String>> blocks) {

    }


}
