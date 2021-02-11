package net.flytre.biome_locator.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class StringList extends EntryListWidget<StringList.StringEntry> {


    private final float percent;

    public StringList(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, float percent) {
        super(client, width, height, top, bottom, itemHeight);
        this.percent = percent;
    }

    public void populate(List<String> mobs) {
        for(String str : mobs) {
            addEntry(new StringEntry(str, client.textRenderer));
        }
    }


    @Override
    public int getRowLeft() {
        float percentModifier = this.width / (1.0f/percent);
        return (int) (this.left + percentModifier - this.getRowWidth() / 2 + 2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int k = getRowLeft();
        if(this.getScrollAmount() > getMaxScroll()) {
            this.setScrollAmount(this.getMaxScroll());
        }
        int l = this.top + 4 - (int) getScrollAmount();
        renderList(matrices, k, l, mouseX, mouseY, delta);
    }

    public static class StringEntry extends EntryListWidget.Entry<StringEntry> {

        private final String toDisplay;
        private final TextRenderer textRenderer;

        public StringEntry(String toDisplay, TextRenderer textRenderer) {
            this.toDisplay = toDisplay;
            this.textRenderer = textRenderer;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            drawCenteredString(matrices,textRenderer,toDisplay,x + entryWidth / 2, y + (entryHeight)/ 2, 0x808080);

        }
    }

}