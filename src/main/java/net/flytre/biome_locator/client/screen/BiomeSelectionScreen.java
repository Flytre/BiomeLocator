package net.flytre.biome_locator.client.screen;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.biome_locator.BiomeDataFetcher;
import net.flytre.biome_locator.BiomeLocator;
import net.flytre.biome_locator.BiomeUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BiomeSelectionScreen extends Screen {

    public final World world;
    private final List<Biome> allBiomes;
    public SearchType searchType;
    private final List<Biome> matchedBiomes;
    private FancyButton searchButton;
    private FancyButton infoButton;
    private FancyButton modeButton;
    private TextFieldWidget searchField;
    private BiomeList selectionList;

    public BiomeSelectionScreen(World world) {
        super(new TranslatableText("item.biome_locator.compass"));
        this.world = world;
        this.allBiomes = BiomeUtils.getAllBiomes(world);
        this.matchedBiomes = new ArrayList<>(allBiomes);
        this.searchType = SearchType.NAME;
    }

    public void search(Biome biome) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeIdentifier(BiomeUtils.getId(biome, world));
        ClientPlayNetworking.send(BiomeLocator.SEARCH_BIOME_PACKET, buf);
    }


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


    public boolean isPauseScreen() {
        return false;
    }


    public void updateSearch() {
        matchedBiomes.clear();
        if (searchType == SearchType.NAME)
            allBiomes.stream().filter(biome -> BiomeUtils.getBiomeName(biome, world).toLowerCase().contains(searchField.getText().toLowerCase())).forEach(matchedBiomes::add);
        else if (searchType == SearchType.RESOURCE)
            allBiomes.stream().filter(biome -> BiomeDataFetcher.hasResource(searchField.getText().toLowerCase(), biome, world)).forEach(matchedBiomes::add);

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
        modeButton.active = BiomeDataFetcher.blocks.size() > 0 || BiomeDataFetcher.mobs.size() > 0;
    }


    @Override
    protected void init() {
        assert client != null;
        client.keyboard.setRepeatEvents(true);

        buttons.clear();
        addButton(new FancyButton(10, height - 30, 110, 20, new TranslatableText("gui.cancel"), (onPress) -> client.openScreen(null)));

        searchButton = addButton(new FancyButton(width - 140, height - 30, 110, 20, new TranslatableText("gui.biome_locator.search"), (onPress) -> {
            if (selectionList.hasSelection()) {
                Biome biome = selectionList.getSelected().getBiome();
                if (biome != null)
                    search(biome);
                client.openScreen(null);
            }
        }));
        infoButton = addButton(new FancyButton(width - 140, height - 60, 110, 20, new TranslatableText("gui.biome_locator.info"), (onPress) -> {
            if (selectionList.hasSelection())
                MinecraftClient.getInstance().openScreen(new BiomeStatDisplay(this, selectionList.getSelected().getBiome()));

        }));
        modeButton = addButton(new FancyButton(10, height - 60, 110, 20, new TranslatableText(searchType == SearchType.NAME ? "gui.biome_locator.searchType.name" : "gui.biome_locator.searchType.resource"), (onPress) -> {
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
        children.add(searchField);

        if (selectionList == null) {
            selectionList = new BiomeList(this, client, width, height, 60, height - 20, 50);
        }
        children.add(selectionList);
        selectionList.refreshList();
    }
}
