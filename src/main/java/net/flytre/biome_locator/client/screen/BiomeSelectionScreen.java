package net.flytre.biome_locator.client.screen;

import net.flytre.biome_locator.client.ClientDataStorage;
import net.flytre.biome_locator.common.BiomeLocator;
import net.flytre.biome_locator.common.BiomeUtils;
import net.flytre.biome_locator.network.SearchBiomeC2SPacket;
import net.flytre.flytre_lib.api.config.reference.BiomeReference;
import net.flytre.flytre_lib.api.gui.button.TranslucentButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BiomeSelectionScreen extends Screen {

    public final World world;
    private final List<Biome> validBiomes;
    private final List<Biome> matchedBiomes;
    public SearchType searchType;
    private TranslucentButton searchButton;
    private TranslucentButton infoButton;
    private TranslucentButton modeButton;
    private TextFieldWidget searchField;
    private BiomeList selectionList;

    public BiomeSelectionScreen(World world) {
        super(new TranslatableText("item.biome_locator.compass"));
        this.world = world;
        this.validBiomes = BiomeUtils
                .getAllBiomes(world)
                .stream()
                .filter(i -> !(new BiomeReference(i, world).isIn(BiomeLocator.CONFIG.getConfig().blacklistedBiomes)))
                .collect(Collectors.toList());
        this.matchedBiomes = new ArrayList<>(validBiomes);
        this.searchType = SearchType.NAME;
    }

    public void search(Biome biome) {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new SearchBiomeC2SPacket(BiomeUtils.getId(biome, world)));
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        selectionList.render(matrices, mouseX, mouseY, delta);
        searchField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean superValue = super.keyPressed(keyCode, scanCode, modifiers);
        if (searchField.isFocused()) {
            updateSearch();
            return true;
        }
        return superValue;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        boolean superValue = super.charTyped(typedChar, keyCode);
        if (searchField.isFocused()) {
            updateSearch();
            return true;
        }
        return superValue;
    }

    @Override
    public void onClose() {
        super.onClose();
        assert client != null;
        client.keyboard.setRepeatEvents(false);
    }

    public void select(BiomeEntry entry) {
        searchButton.active = entry != null;
        infoButton.active = entry != null && modeButton.active;
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }


    public void updateSearch() {
        matchedBiomes.clear();
        if (searchType == SearchType.NAME)
            validBiomes.stream().filter(biome -> BiomeUtils.getBiomeName(biome, world).toLowerCase().contains(searchField.getText().toLowerCase())).forEach(matchedBiomes::add);
        else if (searchType == SearchType.RESOURCE)
            validBiomes.stream().filter(biome -> ClientDataStorage.hasResource(searchField.getText().toLowerCase(), biome, world)).forEach(matchedBiomes::add);

        selectionList.refreshList();
    }

    public String getSearch() {
        return searchField.getText();
    }


    public List<Biome> sort() {
        final List<Biome> biomes = matchedBiomes;
        biomes.sort(Comparator.comparing((biome) -> BiomeUtils.getBiomeName(biome, world)));
        return biomes;
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return selectionList.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void tick() {
        searchField.tick();
        modeButton.active = ClientDataStorage.blocks.size() > 0 || ClientDataStorage.mobs.size() > 0;
    }


    @Override
    protected void init() {
        assert client != null;
        client.keyboard.setRepeatEvents(true);

        List<Element> toRemove = new ArrayList<>();
        children().forEach(i -> {
            if (i instanceof AbstractButton) toRemove.add(i);
        });
        toRemove.forEach(this::remove);

        addDrawableChild(new TranslucentButton(10, height - 30, 110, 20, new TranslatableText("gui.cancel"), (onPress) -> client.setScreen(null)));

        searchButton = addDrawableChild(new TranslucentButton(width - 140, height - 30, 110, 20, new TranslatableText("gui.biome_locator.search"), (onPress) -> {
            if (selectionList.hasSelection()) {
                Biome biome = Objects.requireNonNull(selectionList.getSelectedOrNull()).getBiome();
                if (biome != null)
                    search(biome);
                client.setScreen(null);
            }
        }));
        infoButton = addDrawableChild(new TranslucentButton(width - 140, height - 60, 110, 20, new TranslatableText("gui.biome_locator.info"), (onPress) -> {
            if (selectionList.hasSelection())
                MinecraftClient.getInstance().setScreen(new BiomeStatDisplay(this, Objects.requireNonNull(selectionList.getSelectedOrNull()).getBiome()));

        }));
        modeButton = addDrawableChild(new TranslucentButton(10, height - 60, 110, 20, new TranslatableText(searchType == SearchType.NAME ? "gui.biome_locator.searchType.name" : "gui.biome_locator.searchType.resource"), (onPress) -> {
            if (searchType == SearchType.NAME) {
                searchType = SearchType.RESOURCE;
                onPress.setMessage(new TranslatableText("gui.biome_locator.searchType.resource"));
            } else {
                searchType = SearchType.NAME;
                onPress.setMessage(new TranslatableText("gui.biome_locator.searchType.name"));
            }
            updateSearch();
        }));


        searchButton.active = false;
        infoButton.active = false;

        searchField = new TextFieldWidget(textRenderer, (width - 140) / 2, 10, 140, 20, new TranslatableText("gui.biome_locator.search"));
        addSelectableChild(searchField);

        if (selectionList == null) {
            selectionList = new BiomeList(this, client, width, height, 60, height - 20, 50);
        }
        addSelectableChild(selectionList);
        selectionList.refreshList();

        super.init();
    }
}
