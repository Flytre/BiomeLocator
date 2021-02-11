package net.flytre.biome_locator;

import jdk.internal.jline.internal.Nullable;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class LocatorItem extends Item {
    public LocatorItem(Settings settings) {
        super(settings);
    }

    public static boolean hasPosition(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag dest = tag.getCompound("dest");
        return dest.contains("x") &&
                dest.contains("y") &&
                dest.contains("z");
    }

    public static BlockPos getPosition(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag dest = tag.getCompound("dest");
        int x = dest.contains("x") ? dest.getInt("x") : -10;
        int y = dest.contains("y") ? dest.getInt("y") : -10;
        int z = dest.contains("z") ? dest.getInt("z") : -10;
        return new BlockPos(x, y, z);
    }

    public static Identifier getBiome(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag dest = tag.getCompound("dest");
        String biome = dest.getString("biome");
        if (biome == null) {
            return null;
        }
        return new Identifier(biome);
    }

    public static void startAsyncSearch(ItemStack stack, World world, PlayerEntity user, Biome biome) {
        updateNBT(stack, new BlockPos(-1, -1, -1), null);
        Thread newThread = new Thread(() -> {
            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos blockPos = new BlockPos(user.getBlockPos());
            Identifier biomeID = BiomeUtils.getId(biome, world);
            Biome actual = BiomeUtils.getBiome(biomeID, world);
            BlockPos blockPos2 = serverWorld.locateBiome(actual, blockPos, BiomeLocator.CONFIG.getConfig().getServer().getMaxBiomeDistance(), 8);
            updateNBT(stack, blockPos2, biomeID);
        });
        newThread.start();
    }

    private static void updateNBT(ItemStack stack, @Nullable BlockPos destPos, Identifier biome) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag dest = new CompoundTag();
        if (destPos != null) {
            dest.putInt("x", destPos.getX());
            dest.putInt("y", destPos.getY());
            dest.putInt("z", destPos.getZ());
        } else {
            dest.putInt("x", -11);
            dest.putInt("y", -11);
            dest.putInt("z", -11);
        }
        dest.putString("biome", biome == null ? "none" : biome.toString());
        tag.put("dest", dest);
    }

    public static boolean hasRequiredAdvancement(PlayerEntity user) {
        PlayerAdvancementTracker manager = ((ServerPlayerEntity) user).getAdvancementTracker();
        String advancement = BiomeLocator.CONFIG.getConfig().getServer().getRequiredAdvancement();
        ServerAdvancementLoader loader = ((ServerPlayerEntity) user).server.getAdvancementLoader();
        Advancement adv = loader.get(Identifier.tryParse(advancement));
        return advancement.equals("none") || adv == null || manager.getProgress(adv).isDone();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            String advancement = BiomeLocator.CONFIG.getConfig().getServer().getRequiredAdvancement();
            ServerAdvancementLoader loader = ((ServerPlayerEntity) user).server.getAdvancementLoader();
            Advancement adv = loader.get(Identifier.tryParse(advancement));
            if (hasRequiredAdvancement(user))
                ServerPlayNetworking.send((ServerPlayerEntity) user, BiomeLocator.OPEN_UI_PACKET, PacketByteBufs.empty());
            else {
                assert adv != null;
                Text text = adv.getDisplay() == null ? Text.of(advancement) : adv.getDisplay().getTitle();
                user.sendMessage(new TranslatableText("error.biome_locator.advancement_required", text), false);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
