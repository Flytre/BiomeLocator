package net.flytre.biome_locator.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.flytre.biome_locator.*;
import net.flytre.biome_locator.mixin.ModelPredicateProviderRegistryMixin;
import net.flytre.biome_locator.client.screen.BiomeSelectionScreen;
import net.flytre.biome_locator.client.screen.BiomeStatDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BiomeLocatorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModelPredicateProviderRegistryMixin.register(BiomeLocator.BIOME_LOCATOR, new Identifier("angle"), new ModelPredicateProvider() {
            private final AngleInterpolator value = new AngleInterpolator();
            private final AngleInterpolator speed = new AngleInterpolator();

            public float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity) {
                Entity entity = livingEntity != null ? livingEntity : itemStack.getHolder();
                if (entity == null) {
                    return 0.0F;
                } else {
                    if (clientWorld == null && entity.world instanceof ClientWorld) {
                        clientWorld = (ClientWorld) entity.world;
                    }

                    BlockPos blockPos = LocatorItem.hasPosition(itemStack) ? LocatorItem.getPosition(itemStack) : getSpawnPos(clientWorld); //POS
                    long l = clientWorld.getTime();
                    if (blockPos != null && !(entity.getPos().squaredDistanceTo((double) blockPos.getX() + 0.5D, entity.getPos().getY(), (double) blockPos.getZ() + 0.5D) < 9.999999747378752E-6D)) {
                        boolean bl = livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).isMainPlayer();
                        double e = 0.0D;
                        if (bl) {
                            e = livingEntity.yaw;
                        } else if (entity instanceof ItemFrameEntity) {
                            e = this.getItemFrameAngleOffset((ItemFrameEntity) entity);
                        } else if (entity instanceof ItemEntity) {
                            e = 180.0F - ((ItemEntity) entity).method_27314(0.5F) / 6.2831855F * 360.0F;
                        } else if (livingEntity != null) {
                            e = livingEntity.bodyYaw;
                        }

                        e = MathHelper.floorMod(e / 360.0D, 1.0D);
                        double f = this.getAngleToPos(Vec3d.ofCenter(blockPos), entity) / 6.2831854820251465D;
                        double h;
                        if (bl) {
                            if (this.value.shouldUpdate(l)) {
                                this.value.update(l, 0.5D - (e - 0.25D));
                            }

                            h = f + this.value.value;
                        } else {
                            h = 0.5D - (e - 0.25D - f);
                        }

                        return MathHelper.floorMod((float) h, 1.0F);
                    } else {
                        if (this.speed.shouldUpdate(l)) {
                            this.speed.update(l, Math.random());
                        }

                        double d = this.speed.value + (double) ((float) itemStack.hashCode() / 2.14748365E9F);
                        return MathHelper.floorMod((float) d, 1.0F);
                    }
                }
            }

            @Nullable
            private BlockPos getSpawnPos(ClientWorld world) {
                return world.getDimension().isNatural() ? world.getSpawnPos() : null;
            }

            private double getItemFrameAngleOffset(ItemFrameEntity itemFrame) {
                Direction direction = itemFrame.getHorizontalFacing();
                int i = direction.getAxis().isVertical() ? 90 * direction.getDirection().offset() : 0;
                return MathHelper.wrapDegrees(180 + direction.getHorizontal() * 90 + itemFrame.getRotation() * 45 + i);
            }

            private double getAngleToPos(Vec3d pos, Entity entity) {
                return Math.atan2(pos.getZ() - entity.getZ(), pos.getX() - entity.getX());
            }
        });


        ClientPlayNetworking.registerGlobalReceiver(BiomeLocator.OPEN_UI_PACKET, (client, handler, buf, responseSender) -> {

            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerEntity player = mc.player;
            client.execute(() -> {
                if(player.isSneaking()) {
                    BiomeSelectionScreen selectionScreen = new BiomeSelectionScreen(player.world);
                    Biome biome = MinecraftClient.getInstance().world.getBiome(player.getBlockPos());
                    Identifier id = BiomeUtils.getWorldId(biome, player.world);
                    Biome exact = BiomeUtils.getBiome(id, player.world);
                    mc.openScreen(new BiomeStatDisplay(selectionScreen, exact));
                } else {
                    mc.openScreen(new BiomeSelectionScreen(player.world));
                }
                BiomeDataFetcher.requestCacheIfNeeded();
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(GiantBiomePacket.PACKET_ID, (client, handler, buf, responseSender) -> {

            GiantBiomePacket packet = GiantBiomePacket.fromPacket(buf);
            client.execute(() -> {
                BiomeDataFetcher.mobs = packet.getMobs();
                BiomeDataFetcher.blocks = packet.getBlocks();
            });
        });

        BiomeHud biomeHud = new BiomeHud();
    }


    @Environment(EnvType.CLIENT)
    static class AngleInterpolator {
        private double value;
        private double speed;
        private long lastUpdateTime;

        private AngleInterpolator() {
        }

        private boolean shouldUpdate(long time) {
            return this.lastUpdateTime != time;
        }

        private void update(long time, double d) {
            this.lastUpdateTime = time;
            double e = d - this.value;
            e = MathHelper.floorMod(e + 0.5D, 1.0D) - 0.5D;
            this.speed += e * 0.1D;
            this.speed *= 0.8D;
            this.value = MathHelper.floorMod(this.value + this.speed, 1.0D);
        }
    }
}
