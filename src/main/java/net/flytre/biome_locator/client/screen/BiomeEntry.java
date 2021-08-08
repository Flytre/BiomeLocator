package net.flytre.biome_locator.client.screen;

import net.flytre.biome_locator.common.BiomeUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;

public class BiomeEntry extends EntryListWidget.Entry<BiomeEntry> {

    protected final MinecraftClient client;
    protected final BiomeSelectionScreen selectionScreen;
    protected final Biome biome;
    protected final BiomeList biomesList;
    protected long lastClickTime;

    public BiomeEntry(BiomeList biomesList, Biome biome) {
        this.biomesList = biomesList;
        this.biome = biome;
        this.selectionScreen = biomesList.getSelectionScreen();
        this.client = MinecraftClient.getInstance();
    }


    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        Text precipitation = switch (biome.getPrecipitation()) {
            case RAIN -> new TranslatableText("weather.biome_locator.rain").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff0049bf)));
            case SNOW -> new TranslatableText("weather.biome_locator.snow").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xffbfbfbf)));
            default -> new TranslatableText("weather.biome_locator.none").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff4f4f4f)));
        };

        Text temperature;
        if (biome.getTemperature() <= 0.5) {
            temperature = new TranslatableText("temperature.biome_locator.cold").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xffbfbfbf)));
        } else if (biome.getTemperature() <= 1.5) {
            temperature = new TranslatableText("temperature.biome_locator.medium").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff4f4f4f)));
        } else {
            temperature = new TranslatableText("temperature.biome_locator.warm").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xffc48300)));
        }

        TextColor color = biome.getCategory() == Biome.Category.OCEAN ?
                TextColor.fromRgb(biome.getWaterColor()) :
                biome.getCategory() == Biome.Category.NETHER || biome.getCategory() == Biome.Category.THEEND || biome.getCategory() == Biome.Category.ICY ?
                        TextColor.fromRgb(biome.getFogColor()) :
                        TextColor.fromRgb(biome.getFoliageColor());

        Text name = new LiteralText(BiomeUtils.getBiomeName(biome, selectionScreen.world)).setStyle(Style.EMPTY.withColor(color));
        Text mod = new LiteralText(BiomeUtils.getMod(biome, selectionScreen.world)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x665555FF)).withItalic(true));
        drawCoreInfo(matrices, y, entryHeight, name, temperature, precipitation, mod);
    }

    protected void drawCoreInfo(MatrixStack matrices, int y, int entryHeight, Text name, Text temperature, Text precipitation, Text mod) {
        TextRenderer textRenderer = client.textRenderer;
        double height = textRenderer.fontHeight;
        double requiredHeight = height * 4 + (2 * 3); //3 2 pixel gaps
        double start = (entryHeight + 4 - requiredHeight) / 2;
        drawCenteredText(matrices, name, 0.5f, (int) (y + start), 0xffffff);
        drawCenteredText(matrices, temperature, 0.5f, (int) (y + start + textRenderer.fontHeight + 2), 0x808080);
        drawCenteredText(matrices, precipitation, 0.5f, (int) (y + start + textRenderer.fontHeight * 2 + 4), 0x808080);
        drawCenteredText(matrices, mod, 0.5f, (int) (y + start + textRenderer.fontHeight * 3 + 6), 0x808080);
    }


    protected void drawCenteredText(MatrixStack matrixStack, Text text, float percent, int y, int color) {
        float baseX = (selectionScreen.width / (1.0f / percent));
        client.textRenderer.draw(matrixStack, text, baseX - (client.textRenderer.getWidth(text) / 2.0f), y, color);
    }


    public Biome getBiome() {
        return biome;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            biomesList.selectBiome(this);
            if (Util.getMeasuringTimeMs() - lastClickTime < 250L) {
                selectionScreen.search(biome);
                return true;
            } else {
                lastClickTime = Util.getMeasuringTimeMs();
                return false;
            }
        }
        return false;
    }

}
