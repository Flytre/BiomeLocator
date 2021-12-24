package net.flytre.biome_locator.mixin;


import net.minecraft.network.PacketInflater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PacketInflater.class)
public class PacketInflaterMixin {
    @ModifyConstant(method = "decode", constant = @Constant(intValue = 0x800000))
    private int biome_locator$allowLargePackets(int old) {
        return 0x80000000;
    }
}
