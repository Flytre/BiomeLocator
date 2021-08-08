package net.flytre.biome_locator.config;

import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.api.config.annotation.Description;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;


@DisplayName("config.biome_locator.client")
public class ClientConfig {

    @Description("What corner of the screen the biome tracker overlay is in")
    @SerializedName("ui_location")
    public UILocation uiLocation = UILocation.TOP_LEFT;


    @SerializedName("high_contrast_mode")
    public boolean highContrastMode = false;
}
