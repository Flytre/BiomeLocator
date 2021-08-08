package net.flytre.biome_locator.config;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.api.config.annotation.Description;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;
import net.flytre.flytre_lib.api.config.annotation.Range;
import net.flytre.flytre_lib.api.config.network.SyncedConfig;
import net.flytre.flytre_lib.api.config.reference.AdvancementReference;
import net.flytre.flytre_lib.api.config.reference.BiomeReference;

import java.util.Set;

@DisplayName("config.biome_locator.server")
public class ServerConfig implements SyncedConfig {


    @Range(min = 0, max = 25600)
    @Description("The farthest distance the biome locator will look to try and find the target biome")
    @SerializedName("max_biome_distance")
    public int maxBiomeDistance = 6400;


    @Description("Use the /advancement command for help with advancement ids")
    @SerializedName("required_advancement")
    public AdvancementReference requiredAdvancement = new AdvancementReference("minecraft", "none");

    @Range(min = 0, max = 1f)
    @Description("The chance of the biome locator appearing in loot chests")
    @SerializedName("chance_as_loot")
    public float chanceAsLoot = 0.16f;

    @Description("The list of biomes that are hidden from the biome locator")
    @SerializedName("blacklisted_biomes")
    public Set<BiomeReference> blacklistedBiomes = Set.of(
            new BiomeReference("appliedenergistics2", "spatial_storage")
    );
}

