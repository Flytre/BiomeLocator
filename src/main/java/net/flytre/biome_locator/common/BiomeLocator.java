package net.flytre.biome_locator.common;

import net.fabricmc.api.ModInitializer;
import net.flytre.biome_locator.config.ServerConfig;
import net.flytre.biome_locator.network.GiantBiomeS2CPacket;
import net.flytre.biome_locator.network.OpenUiS2CPacket;
import net.flytre.biome_locator.network.RequestDataC2SPacket;
import net.flytre.biome_locator.network.SearchBiomeC2SPacket;
import net.flytre.biome_locator.server.LootTable;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BiomeLocator implements ModInitializer {

    public static final BiomeLocatorItem BIOME_LOCATOR = new BiomeLocatorItem(new Item.Settings().group(ItemGroup.TOOLS));


    public static final ConfigHandler<ServerConfig> CONFIG = new ConfigHandler<>(new ServerConfig(), "biome_locator_server","config.biome_locator");

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("biome_locator", "compass"), BIOME_LOCATOR);


        PacketUtils.registerS2CPacket(GiantBiomeS2CPacket.class, GiantBiomeS2CPacket::new);
        PacketUtils.registerS2CPacket(OpenUiS2CPacket.class, (__) -> new OpenUiS2CPacket());
        PacketUtils.registerC2SPacket(RequestDataC2SPacket.class, (__) -> new RequestDataC2SPacket());
        PacketUtils.registerC2SPacket(SearchBiomeC2SPacket.class, SearchBiomeC2SPacket::new);


        ConfigRegistry.registerServerConfig(CONFIG);
        CONFIG.handle();
        LootTable.init();
    }
}
