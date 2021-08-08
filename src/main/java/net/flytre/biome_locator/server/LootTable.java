package net.flytre.biome_locator.server;

import net.flytre.biome_locator.common.BiomeLocator;
import net.flytre.flytre_lib.api.event.LootProcessingEvent;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

import java.util.Set;

public class LootTable {

    private static final Set<Identifier> CHESTS = Set.of(
            new Identifier("minecraft", "chests/abandoned_mineshaft"),
            new Identifier("minecraft", "chests/buried_treasure"),
            new Identifier("minecraft", "chests/desert_pyramid"),
            new Identifier("minecraft", "chests/jungle_temple"),
            new Identifier("minecraft", "chests/pillager_outpost"),
            new Identifier("minecraft", "chests/shipwreck_map"),
            new Identifier("minecraft", "chests/stronghold_library")
    );


    public static void init() {

        LootProcessingEvent.EVENT.register((resourceManager, manager, id, tableBuilder, setter) -> {
            LootPool.Builder poolBuilder = new LootPool.Builder();
            poolBuilder.conditionally(RandomChanceLootCondition.builder(BiomeLocator.CONFIG.getConfig().chanceAsLoot));
            poolBuilder.with(ItemEntry.builder(BiomeLocator.BIOME_LOCATOR));

            if (CHESTS.contains(id))
                tableBuilder.pool(poolBuilder);
        });

    }
}
