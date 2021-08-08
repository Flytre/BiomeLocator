package net.flytre.biome_locator.client.screen;

import net.flytre.biome_locator.common.BiomeUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.world.biome.Biome;

public class RabbitBiomeEntry extends BiomeEntry {
    public RabbitBiomeEntry(BiomeList biomesList, Biome biome) {
        super(biomesList, biome);
    }


    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        Text precipitation = new TranslatableText("weather.biome_locator.rabbit").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xffcc6900)));
        Text temperature = new TranslatableText("temperature.biome_locator.rabbit").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xffbfbfbf)));

        TextColor color = TextColor.fromRgb(0xdeb68c);
        Text name = new TranslatableText("biome.biome_locator.rabbit_land").setStyle(Style.EMPTY.withColor(color));
        Text mod = new LiteralText(BiomeUtils.getModFromModId("biome_locator")).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x665555FF)).withItalic(true));
        drawCoreInfo(matrices, y, entryHeight, name, temperature, precipitation, mod);
    }


}
