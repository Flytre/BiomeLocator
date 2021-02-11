package net.flytre.biome_locator;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class LootTable {
    static final LootPool POOL;

    static final Set<Identifier> IDS;

    static {
        FabricLootPoolBuilder POOL_BUILDER = FabricLootPoolBuilder.builder();
        POOL_BUILDER.withCondition(RandomChanceLootCondition.builder(BiomeLocator.CONFIG.getConfig().getServer().getChanceAsLoot()).build());
        POOL_BUILDER.withEntry(ItemEntry.builder(BiomeLocator.BIOME_LOCATOR).build());
        POOL = POOL_BUILDER.build();

        IDS = new HashSet<>();
        IDS.add(new Identifier("minecraft", "chests/abandoned_mineshaft"));
        IDS.add(new Identifier("minecraft", "chests/buried_treasure"));
        IDS.add(new Identifier("minecraft", "chests/desert_pyramid"));
        IDS.add(new Identifier("minecraft", "chests/jungle_temple"));
        IDS.add(new Identifier("minecraft", "chests/pillager_outpost"));
        IDS.add(new Identifier("minecraft", "chests/shipwreck_map"));
        IDS.add(new Identifier("minecraft", "chests/stronghold_library"));

    }

    public static void init() {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (IDS.stream().anyMatch(i -> i.equals(id))) {
                supplier.withPool(POOL);
            }
        });
    }
}
