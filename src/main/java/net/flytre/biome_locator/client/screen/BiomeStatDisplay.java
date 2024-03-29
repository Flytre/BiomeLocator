package net.flytre.biome_locator.client.screen;

import net.flytre.biome_locator.client.ClientDataStorage;
import net.flytre.biome_locator.common.BiomeUtils;
import net.flytre.biome_locator.mixin.PlacedFeatureAccessor;
import net.flytre.flytre_lib.api.base.util.FakeWorld;
import net.flytre.flytre_lib.api.gui.button.TranslucentButton;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BiomeStatDisplay extends Screen {

    private final BiomeSelectionScreen selectionScreen;
    private final Biome biome;
    private final String precipitation;
    private final String temperature;
    private final String rainfall;
    private final String highHumidity;
    private final boolean rabbit;
    private TranslucentButton nextButton;

    public BiomeStatDisplay(BiomeSelectionScreen selectionScreen, Biome biome) {
        super(new LiteralText(I18n.translate(BiomeUtils.getBiomeName(biome == null ? BiomeUtils.getBiome(new Identifier("minecraft:desert"), selectionScreen.world) : biome, selectionScreen.world))));
        this.selectionScreen = selectionScreen;
        this.biome = biome != null ? biome : BiomeUtils.getBiome(new Identifier("minecraft:desert"), selectionScreen.world);
        this.rabbit = biome == null;
        biome = this.biome;

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
        Identifier id = BiomeUtils.getId(biome, getSelectionScreen().world);
        nextButton.active = ClientDataStorage.mobs.get(id) != null;
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

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.precipitation"), width / 2.0f - 100, 70, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(precipitation), width / 2.0f - 100, 80, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.rainfall"), width / 2.0f - 100, 100, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(rainfall), width / 2.0f - 100, 110, 0x808080);


        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.temperature"), width / 2.0f + 40, 70, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(temperature), width / 2.0f + 40, 80, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.high_humidity"), width / 2.0f + 40, 100, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(highHumidity), width / 2.0f + 40, 110, 0x808080);

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.sky_color"), width / 2.0f - 100, 130, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(String.format("#%06X", biome.getSkyColor())), width / 2.0f - 100, 140, biome.getSkyColor());

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.fog_color"), width / 2.0f + 40, 130, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(String.format("#%06X", biome.getFogColor())), width / 2.0f + 40, 140, biome.getFogColor());

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.water_color"), width / 2.0f - 100, 160, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(String.format("#%06X", biome.getWaterColor())), width / 2.0f - 100, 170, biome.getWaterColor());

        textRenderer.draw(matrixStack, new TranslatableText("gui.biome_locator.foliage_color"), width / 2.0f + 40, 160, 0xffffff);
        textRenderer.draw(matrixStack, new LiteralText(String.format("#%06X", biome.getFoliageColor())), width / 2.0f + 40, 170, biome.getFoliageColor());


        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }


    public void init() {
        List<Element> toRemove = new ArrayList<>();
        children().forEach(i -> {
            if (i instanceof AbstractButton) toRemove.add(i);
        });
        toRemove.forEach(this::remove);

        addDrawableChild(new TranslucentButton(10, height - 30, 110, 20, new TranslatableText("gui.biome_locator.back"), (onPress) -> {
            MinecraftClient.getInstance().setScreen(selectionScreen);
            selectionScreen.updateSearch();
        }));
        addDrawableChild(new TranslucentButton(width - 120, height - 30, 110, 20, new TranslatableText("gui.biome_locator.search"), (onPress) -> {
            selectionScreen.search(biome);
            assert client != null;
            client.setScreen(null);
        }));

        nextButton = addDrawableChild(new TranslucentButton(width - 120, height - 60, 110, 20, new TranslatableText("gui.biome_locator.next"), (onPress) -> MinecraftClient.getInstance().setScreen(new BiomeStatDisplay2(this))));

        super.init();
    }
}
