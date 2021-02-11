package net.flytre.biome_locator.client.screen;

import net.flytre.biome_locator.BiomeUtils;
import net.minecraft.client.font.TextRenderer;
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

        TextRenderer textRenderer = client.textRenderer;

        TextColor color = TextColor.fromRgb(0xdeb68c);
        Text name = new TranslatableText("biome.biome_locator.rabbit_land").setStyle(Style.EMPTY.withColor(color));
        Text src = new LiteralText(BiomeUtils.getModFromModId("biome_locator")).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x665555FF)).withItalic(true));

        double height = textRenderer.fontHeight;
        double requiredHeight = height * 4 + (2 * 3); //3 2 pixel gaps
        double start = (entryHeight + 4 - requiredHeight) / 2;
        drawCenteredText(matrices, name, 0.5f, (int) (y + start), 0xffffff);
        drawCenteredText(matrices, temperature, 0.5f, (int) (y + start + textRenderer.fontHeight + 2), 0x808080);
        drawCenteredText(matrices, precipitation, 0.5f, (int) (y + start + textRenderer.fontHeight * 2 + 4), 0x808080);
        drawCenteredText(matrices, src, 0.5f, (int) (y + start + textRenderer.fontHeight * 3 + 6), 0x808080);
    }


}
