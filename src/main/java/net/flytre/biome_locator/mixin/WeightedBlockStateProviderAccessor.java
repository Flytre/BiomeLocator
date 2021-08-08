package net.flytre.biome_locator.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedBlockStateProvider.class)
public interface WeightedBlockStateProviderAccessor {

    @Accessor(value = "states")
    DataPool<BlockState> getList();
}
