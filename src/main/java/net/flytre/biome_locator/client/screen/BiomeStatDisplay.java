package net.flytre.biome_locator.client.screen;

import net.flytre.biome_locator.BiomeDataFetcher;
import net.flytre.biome_locator.BiomeUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class BiomeStatDisplay extends Screen {

    private final BiomeSelectionScreen selectionScreen;
    private final Biome biome;
    private final String top;
    private final String under;
    private final String baseHeight;
    private final String heightVariation;
    private final String precipitation;
    private final String temperature;
    private final String rainfall;
    private final String highHumidity;
    private FancyButton nextButton;
    private final boolean rabbit;

    public BiomeStatDisplay(BiomeSelectionScreen selectionScreen, Biome biome) {
        super(new LiteralText(I18n.translate(BiomeUtils.getBiomeName(biome == null ? BiomeUtils.getBiome(new Identifier("minecraft:desert"),selectionScreen.world) : biome , selectionScreen.world))));
        this.selectionScreen = selectionScreen;
        this.biome = biome != null ? biome : BiomeUtils.getBiome(new Identifier("minecraft:desert"), selectionScreen.world);
        this.rabbit = biome == null;
        biome = this.biome;
        this.top = I18n.translate(biome.getGenerationSettings().getSurfaceConfig().getTopMaterial().getBlock().getTranslationKey());
        this.under = I18n.translate(biome.getGenerationSettings().getSurfaceConfig().getUnderMaterial().getBlock().getTranslationKey());

        if (biome.getDepth() < -1) {
            baseHeight = I18n.translate("descriptor.biome_locator.very_low");
        } else if (biome.getDepth() < 0) {
            baseHeight = I18n.translate("descriptor.biome_locator.low");
        } else if (biome.getDepth() < 0.4) {
            baseHeight = I18n.translate("descriptor.biome_locator.average");
        } else if (biome.getDepth() < 1) {
            baseHeight = I18n.translate("descriptor.biome_locator.high");
        } else {
            baseHeight = I18n.translate("descriptor.biome_locator.very_high");
        }

        if (biome.getScale() < 0.3) {
            heightVariation = I18n.translate("descriptor.biome_locator.average");
        } else if (biome.getScale() < 0.6) {
            heightVariation = I18n.translate("descriptor.biome_locator.high");
        } else {
            heightVariation = I18n.translate("descriptor.biome_locator.very_high");
        }

        if (biome.getPrecipitation() == Biome.Precipitation.SNOW) {
            precipitation = I18n.translate("weather.biome_locator.snow");
        } else if (biome.getPrecipitation() == Biome.Precipitation.RAIN) {
            precipitation = I18n.translate("weather.biome_locator.rain");
        } else {
            precipitation = I18n.translate("weather.biome_locator.none");
        }

        if (biome.getTemperature() <= 0.5) {
            temperature = I18n.translate("temperature.biome_locator.cold");
        } else if (biome.getTemperature() <= 1.5) {
            temperature = I18n.translate("temperature.biome_locator.medium");
        } else {
            temperature = I18n.translate("temperature.biome_locator.warm");
        }

        if (biome.getDownfall() <= 0) {
            rainfall = I18n.translate("descriptor.biome_locator.none");
        } else if (biome.getDownfall() < 0.2) {
            rainfall = I18n.translate("descriptor.biome_locator.very_low");
        } else if (biome.getDownfall() < 0.3) {
            rainfall = I18n.translate("descriptor.biome_locator.low");
        } else if (biome.getDownfall() < 0.5) {
            rainfall = I18n.translate("descriptor.biome_locator.average");
        } else if (biome.getDownfall() < 0.85) {
            rainfall = I18n.translate("descriptor.biome_locator.high");
        } else {
            rainfall = I18n.translate("descriptor.biome_locator.very_high");
        }

        this.highHumidity = biome.hasHighHumidity() ? I18n.translate("gui.yes") : I18n.translate("gui.no");
    }

    public boolean isRabbit() {
        return rabbit;
    }

    @Override
    public void tick() {
        super.tick();
        Identifier id = BiomeUtils.getId(biome,getSelectionScreen().world);
        nextButton.active = BiomeDataFetcher.mobs.get(id) != null;
    }

    public BiomeSelectionScreen getSelectionScreen() {
        return selectionScreen;
    }

    public Biome getBiome() {
        return biome;
    }

    public boolean isPauseScreen() {
        return false;
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        String name = rabbit ? I18n.translate("biome.biome_locator.rabbit_land") : BiomeUtils.getBiomeName(biome, selectionScreen.world);
        textRenderer.draw(matrixStack, new LiteralText(name), (width / 2.0f) - (textRenderer.getWidth(name) / 2.0f), 20, 0xffffff);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.top_block"), width / 2.0f - 100, 40, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(top), width / 2.0f - 100, 50, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.precipitation"), width / 2.0f - 100, 70, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(precipitation), width / 2.0f - 100, 80, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.base_height"), width / 2.0f - 100, 100, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(baseHeight), width / 2.0f - 100, 110, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.rainfall"), width / 2.0f - 100, 130, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(rainfall), width / 2.0f - 100, 140, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.under_block"), width / 2.0f + 40, 40, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(under), width / 2.0f + 40, 50, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.temperature"), width / 2.0f + 40, 70, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(temperature), width / 2.0f + 40, 80, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.height_variation"), width / 2.0f + 40, 100, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(heightVariation), width / 2.0f + 40, 110, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.high_humidity"), width / 2.0f + 40, 130, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(highHumidity), width / 2.0f + 40, 140, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.sky_color"), width / 2.0f - 100, 160, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(String.format("#%06X", biome.getSkyColor())), width / 2.0f - 100, 170, biome.getSkyColor());

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.fog_color"), width / 2.0f + 40, 160, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(String.format("#%06X", biome.getFogColor())), width / 2.0f + 40, 170, biome.getFogColor());

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.water_color"), width / 2.0f - 100, 190, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(String.format("#%06X", biome.getWaterColor())), width / 2.0f - 100, 200, biome.getWaterColor());

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.foliage_color"), width / 2.0f + 40, 190, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(String.format("#%06X", biome.getFoliageColor())), width / 2.0f + 40, 200, biome.getFoliageColor());



        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }


    public void init() {
        buttons.clear();
        addButton(new FancyButton(10, height - 30, 110, 20, new TranslatableText("gui.biome_locator.back"), (onPress) -> {
            MinecraftClient.getInstance().openScreen(selectionScreen);
            selectionScreen.updateSearch();
        }));
        addButton(new FancyButton(width - 120, height - 30, 110, 20, new TranslatableText("gui.biome_locator.search"), (onPress) -> {
            selectionScreen.search(biome);
            assert client != null;
            client.openScreen(null);
        }));

        nextButton = addButton(new FancyButton(width - 120, height - 60, 110, 20, new TranslatableText("gui.biome_locator.next"), (onPress) -> MinecraftClient.getInstance().openScreen(new BiomeStatDisplay2(this))));
    }
}
