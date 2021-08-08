package net.flytre.biome_locator.client.screen;

import net.flytre.biome_locator.client.ClientDataStorage;
import net.flytre.biome_locator.common.BiomeUtils;
import net.flytre.flytre_lib.api.gui.button.TranslucentButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BiomeStatDisplay2 extends Screen {

    private final BiomeStatDisplay page1;
    private final List<String> mobs;
    private final List<String> resources;
    private final Biome biome;
    private StringList mobList;
    private StringList resourceList;

    public BiomeStatDisplay2(BiomeStatDisplay page1) {
        super(new LiteralText(I18n.translate(BiomeUtils.getBiomeName(page1.getBiome(), page1.getSelectionScreen().world))));
        this.biome = page1.getBiome();
        this.page1 = page1;
        Identifier id = BiomeUtils.getId(biome,page1.getSelectionScreen().world);

        if(!page1.isRabbit()) {
            this.mobs = new ArrayList<>(ClientDataStorage.mobs.get(id)).stream().map(I18n::translate).distinct().collect(Collectors.toList());
            this.resources = new ArrayList<>(ClientDataStorage.blocks.get(id)).stream().map(I18n::translate).distinct().collect(Collectors.toList());
        } else {
            this.mobs = new ArrayList<>(Collections.singleton("entity.minecraft.rabbit")).stream().map(I18n::translate).distinct().collect(Collectors.toList());
            this.resources = new ArrayList<>(Collections.singleton("block.biome_locator.rabbit_ore")).stream().map(I18n::translate).distinct().collect(Collectors.toList());
        }
        Collections.sort(mobs);
        Collections.sort(resources);
    }

    public boolean isPauseScreen() {
        return false;
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredText(matrixStack, page1.isRabbit() ? new TranslatableText("biome.biome_locator.rabbit_land") : new LiteralText(BiomeUtils.getBiomeName(biome, page1.getSelectionScreen().world)), 0.5f, 20, 0xffffff);
        drawCenteredText(matrixStack, new TranslatableText("gui.biome_locator.mobs"), 0.25f, 40, 0xffffff);
        drawCenteredText(matrixStack, new TranslatableText("gui.biome_locator.resources"), 0.75f, 40, 0xffffff);
        mobList.render(matrixStack, mouseX, mouseY, partialTicks);
        resourceList.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        mobList.mouseScrolled(mouseX, mouseY, amount);
        resourceList.mouseScrolled(mouseX, mouseY, amount);
        return true;
    }

    private void drawCenteredText(MatrixStack matrixStack, Text text, float percent, int y, int color) {
        float baseX = (width / (1.0f / percent));
        textRenderer.draw(matrixStack, text, baseX - (textRenderer.getWidth(text) / 2.0f), y, color);
    }


    public void init() {

        List<Element> toRemove = new ArrayList<>();
        children().forEach(i -> {if(i instanceof AbstractButton) toRemove.add(i);});
        toRemove.forEach(this::remove);

        addDrawableChild(new TranslucentButton(10, height - 30, 110, 20, new TranslatableText("gui.biome_locator.previous"), (onPress) -> MinecraftClient.getInstance().setScreen(page1)));
        addDrawableChild(new TranslucentButton(width - 120, height - 30, 110, 20, new TranslatableText("gui.biome_locator.search"), (onPress) -> {
            page1.getSelectionScreen().search(biome);
            assert client != null;
            client.setScreen(null);
        }));

        if (mobList == null) {
            mobList = new StringList(client, width, height, 60, height - 60, 12, 0.25f);
        }
        mobList.populate(mobs);

        if (resourceList == null) {
            resourceList = new StringList(client, width, height, 60, height - 60, 12, 0.75f);
        }
        resourceList.populate(resources);
    }
}
