package net.flytre.biome_locator.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.biome.Biome;

import java.util.Objects;

public class BiomeList extends EntryListWidget<BiomeEntry> {

    private final BiomeSelectionScreen selectionScreen;

    public BiomeList(BiomeSelectionScreen selectionScreen, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.selectionScreen = selectionScreen;
    }

    public BiomeSelectionScreen getSelectionScreen() {
        return selectionScreen;
    }


    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 20;
    }

    @Override
    public int getRowWidth() {
        return 180;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int k = getRowLeft();
        if (this.getScrollAmount() > getMaxScroll()) {
            this.setScrollAmount(this.getMaxScroll());
        }
        int l = this.top + 4 - (int) getScrollAmount();
        renderList(matrices, k, l, mouseX, mouseY, delta);
    }

    protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
        int i = this.getItemCount();

        for (int j = 0; j < i; ++j) {
            int k = this.getRowTop(j);
            int l = this.getRowTop(j) + itemHeight;
            if (l >= this.top && k <= this.bottom) {
                int n = this.itemHeight - 4;
                BiomeEntry entry = this.getEntry(j);
                int o = this.getRowWidth();
                int r;

                if(isSelectedItem(j)) {
                    r = this.left + this.width / 2 - o / 2;
                    int q = this.left + this.width / 2 + o / 2;
                    DrawableHelper.fill(matrices, r + 1, k, q - 1, k + 1, 0x994f4f4f);
                    DrawableHelper.fill(matrices, r + 1, l - 1, q - 1, l, 0x994f4f4f);
                    DrawableHelper.fill(matrices, r, k, r + 1, l, 0x994f4f4f);
                    DrawableHelper.fill(matrices, q - 1, k, q, l, 0x994f4f4f);
                    DrawableHelper.fill(matrices, r + 1, k + 1, q - 1, l - 1, 0x44ababab);
                }
                r = this.getRowLeft();
                entry.render(matrices, j, k, r, o, n, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPosition(mouseX, mouseY), entry), delta);
            }
        }

    }


    public boolean hasSelection() {
        return getSelected() != null;
    }

    public void selectBiome(BiomeEntry entry) {
        setSelected(entry);
        selectionScreen.select(entry);
    }

    public void refreshList() {
        clearEntries();

        if(selectionScreen.getSearch().equalsIgnoreCase("rabbit") && selectionScreen.searchType == SearchType.NAME) {
            addEntry(new RabbitBiomeEntry(this,null));
            return;
        }

        for (Biome biome : selectionScreen.sort()) {
            addEntry(new BiomeEntry(this, biome));
        }
        selectBiome(null);
    }
}
