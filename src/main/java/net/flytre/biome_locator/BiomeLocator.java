package net.flytre.biome_locator;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.biome_locator.config.Config;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BiomeLocator implements ModInitializer {

    public static final LocatorItem BIOME_LOCATOR = new LocatorItem(new FabricItemSettings().group(ItemGroup.TOOLS));

    public static final Identifier SEARCH_BIOME_PACKET = new Identifier("biome_locator:search_biome");
    public static final Identifier OPEN_UI_PACKET = new Identifier("biome_locator:open_ui");
    public static final Identifier REQUEST_CACHE = new Identifier("biome_locator:request_cache");

    public static final ConfigHandler<Config> CONFIG = new ConfigHandler<>(new Config(), "biome_locator");

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("biome_locator", "compass"), BIOME_LOCATOR);

        ServerPlayNetworking.registerGlobalReceiver(SEARCH_BIOME_PACKET, (server, player, handler, buf, responseSender) -> {
            Identifier id = buf.readIdentifier();
            server.execute(() -> {
                if (LocatorItem.hasRequiredAdvancement(player))
                    LocatorItem.startAsyncSearch(player.getOffHandStack().getItem() == BIOME_LOCATOR ? player.getOffHandStack() : player.getMainHandStack(), player.world, player, BiomeUtils.getBiome(id, player.world));
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_CACHE, (server, player, handler, buf, responseSender) -> server.execute(() -> BiomeDataFetcher.receivedCacheRequest(player, player.world)));
        ConfigRegistry.registerServerConfig(CONFIG);
        CONFIG.handle();
        LootTable.init();
    }
}
