package net.flytre.biome_locator.common;

import net.flytre.biome_locator.network.OpenUiS2CPacket;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
import org.jetbrains.annotations.Nullable;

public class BiomeLocatorItem extends Item {
    public BiomeLocatorItem(Settings settings) {
        super(settings);
    }

    public static boolean hasPosition(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound dest = nbt.getCompound("dest");
        return dest.contains("x") &&
                dest.contains("y") &&
                dest.contains("z");
    }

    public static BlockPos getPosition(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound dest = nbt.getCompound("dest");
        int x = dest.contains("x") ? dest.getInt("x") : -10;
        int y = dest.contains("y") ? dest.getInt("y") : -10;
        int z = dest.contains("z") ? dest.getInt("z") : -10;
        return new BlockPos(x, y, z);
    }

    public static Identifier getBiome(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound dest = nbt.getCompound("dest");
        String biome = dest.getString("biome");
        if (biome == null) {
            return null;
        }
        return new Identifier(biome);
    }

    public static void startThreadedSearch(ItemStack stack, World world, PlayerEntity user, Biome biome) {
        updateNBT(stack, new BlockPos(-1, -1, -1), null);
        Thread newThread = new Thread(() -> {
            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos startPos = new BlockPos(user.getBlockPos());
            Identifier biomeID = BiomeUtils.getId(biome, world);
            Biome actual = BiomeUtils.getBiome(biomeID, world);
            BlockPos destinationPos = serverWorld.locateBiome(actual, startPos, BiomeLocator.CONFIG.getConfig().maxBiomeDistance, 8);
            updateNBT(stack, destinationPos, biomeID);
        });
        newThread.start();
    }

    private static void updateNBT(ItemStack stack, @Nullable BlockPos destPos, Identifier biome) {
        NbtCompound Nbt = stack.getOrCreateNbt();
        NbtCompound dest = new NbtCompound();
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
        Nbt.put("dest", dest);
    }

    public static boolean hasRequiredAdvancement(PlayerEntity user) {
        return BiomeLocator.CONFIG.getConfig().requiredAdvancement.hasAdvancement((ServerPlayerEntity) user, (ServerWorld) user.world, true);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            Advancement adv = BiomeLocator.CONFIG.getConfig().requiredAdvancement.getValue(world);
            if (hasRequiredAdvancement(user))
                ((ServerPlayerEntity)user).networkHandler.sendPacket(new OpenUiS2CPacket());
            else {
                assert adv != null;
                Text text = adv.getDisplay() == null ? Text.of(adv.getId() + "") : adv.getDisplay().getTitle();
                user.sendMessage(new TranslatableText("error.biome_locator.advancement_required", text), false);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
