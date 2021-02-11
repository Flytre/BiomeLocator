package net.flytre.biome_locator.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedBlockStateProvider.class)
public interface WeightedBlockStateProviderAccessor {

    @Accessor(value = "states")
    WeightedList<BlockState> getList();
}
