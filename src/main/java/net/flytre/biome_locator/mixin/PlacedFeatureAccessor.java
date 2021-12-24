package net.flytre.biome_locator.mixin;


import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(PlacedFeature.class)
public interface PlacedFeatureAccessor {


    @Accessor("feature")
    Supplier<ConfiguredFeature<?, ?>> getFeature();
}
