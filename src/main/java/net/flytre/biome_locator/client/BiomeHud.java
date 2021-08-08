package net.flytre.biome_locator.client;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.biome_locator.common.BiomeUtils;
import net.flytre.biome_locator.common.BiomeLocatorItem;
import net.flytre.biome_locator.config.ClientConfig;
import net.flytre.biome_locator.config.UILocation;
import net.flytre.flytre_lib.api.gui.Hud;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class BiomeHud extends Hud {


    private void draw(TextRenderer renderer, MatrixStack matrices, Text text, float x, float y, int color, UILocation location) {

        if (isLeftAligned(location)) {
            renderer.draw(matrices, text, x, y, color);
        } else {
            int width = renderer.getWidth(text);
            renderer.draw(matrices, text, x - width, y, color);
        }

    }

    private boolean isLeftAligned(UILocation location) {
        return location == UILocation.TOP_LEFT || location == UILocation.BOTTOM_LEFT;
    }

    @Override
    public void draw(MatrixStack matrixStack, PlayerEntity player, TextRenderer textRenderer, ItemRenderer itemRenderer, int maxX, int maxY) {

        ItemStack stack;

        if (player == null)
            return;

        stack = player.getOffHandStack();

        if (!(stack.getItem() instanceof BiomeLocatorItem))
            stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof BiomeLocatorItem))
            return;

        if (client.world == null)
            return;


        Function<Integer, Integer> xPos = null;
        Function<Integer, Integer> yPos = null;

        ClientConfig config = BiomeLocatorClient.CONFIG.getConfig();

        UILocation location = config.uiLocation;

        switch (location) {
            case TOP_LEFT -> {
                xPos = (i) -> 10;
                yPos = (i) -> (i + 1) * 15 + (i % 2 == 0 ? -5 : -10);
            }
            case TOP_RIGHT -> {
                xPos = (i) -> maxX - 10;
                yPos = (i) -> (i + 1) * 15 + (i % 2 == 0 ? -5 : -10);
            }
            case BOTTOM_LEFT -> {
                xPos = (i) -> 10;
                yPos = (i) -> maxY - 130 + ((i + 1) * 15 + (i % 2 == 0 ? -5 : -10));
            }
            case BOTTOM_RIGHT -> {
                xPos = (i) -> maxX - 10;
                yPos = (i) -> maxY - 130 + ((i + 1) * 15 + (i % 2 == 0 ? -5 : -10));
            }
        }

        int color = config.highContrastMode ? -1 : 0xFF5c5c5c;

        if (BiomeLocatorItem.hasPosition(stack)) {
            BlockPos pos = BiomeLocatorItem.getPosition(stack);
            if (pos.equals(new BlockPos(-1, -1, -1))) {
                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.status"), xPos.apply(0), yPos.apply(0), -1, location);
                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.searching"), xPos.apply(1), yPos.apply(1), color, location);
            } else if (pos.equals(new BlockPos(-11, -11, -11))) {
                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.status"), xPos.apply(0), yPos.apply(0), -1, location);
                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.not_found"), xPos.apply(1), yPos.apply(1), color, location);
            } else {
                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.status"), xPos.apply(0), yPos.apply(0), -1, location);
                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.found"), xPos.apply(1), yPos.apply(1), color, location);
                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.biome"), xPos.apply(2), yPos.apply(2), -1, location);
                Identifier biome = BiomeLocatorItem.getBiome(stack);
                draw(textRenderer, matrixStack, Text.of(biome == null ? "Error" : BiomeUtils.getBiomeName(biome)), xPos.apply(3), yPos.apply(3), color, location);

                String formattedCoordinates = pos.getX() + ", " + pos.getZ();
                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.coordinates"), xPos.apply(4), yPos.apply(4), -1, location);
                draw(textRenderer, matrixStack, Text.of(formattedCoordinates), xPos.apply(5), yPos.apply(5), color, location);

                draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.distance"), xPos.apply(6), yPos.apply(6), -1, location);
                int distance = (int) Math.sqrt(pos.getSquaredDistance(player.getX(), pos.getY(), player.getZ(), false));

                Biome playerBiome = client.world.getBiome(player.getBlockPos());
                Identifier playerBiomeId = BiomeUtils.getId(playerBiome, client.world);


                if (playerBiomeId.equals(biome))
                    draw(textRenderer, matrixStack, new TranslatableText("hud.biome_locator.reached"), xPos.apply(7), yPos.apply(7), color, location);
                else
                    draw(textRenderer, matrixStack, Text.of("" + distance), xPos.apply(7), yPos.apply(7), color, location);
            }
        }
    }

}
