package satisfyu.vinery.world;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import satisfyu.vinery.VineryIdentifier;

public class VineryConfiguredFeatures {
	public static final ResourceKey<ConfiguredFeature<?, ?>> CHERRY_KEY = registerKey("cherry");

	public static final ResourceKey<ConfiguredFeature<?, ?>> CHERRY_VARIANT_KEY = registerKey("cherry_variant");

	public static final ResourceKey<ConfiguredFeature<?, ?>> OLD_CHERRY_KEY = registerKey("old_cherry");

	public static final ResourceKey<ConfiguredFeature<?, ?>> OLD_CHERRY_BEE_KEY = registerKey("old_cherry_bee");

	public static final ResourceKey<ConfiguredFeature<?, ?>> OLD_CHERRY_VARIANT_KEY = registerKey("old_cherry_variant");

	public static final ResourceKey<ConfiguredFeature<?, ?>> OLD_CHERRY_VARIANT_WITH_BEE_KEY = registerKey(
			"old_cherry_variant_with_bee");

	public static final ResourceKey<ConfiguredFeature<?, ?>> RED_GRAPE_BUSH_PATCH_KEY = registerKey("red_grape_bush");

	public static final ResourceKey<ConfiguredFeature<?, ?>> WHITE_GRAPE_BUSH_PATCH_KEY = registerKey(
			"white_grape_bush");

	public static final ResourceKey<ConfiguredFeature<?, ?>> TAIGA_RED_GRAPE_BUSH_PATCH_KEY = registerKey(
			"taiga_red_grape_bush");

	public static final ResourceKey<ConfiguredFeature<?, ?>> TAIGA_WHITE_GRAPE_BUSH_PATCH_KEY = registerKey(
			"taiga_white_grape_bush");

	public static final ResourceKey<ConfiguredFeature<?, ?>> SAVANNA_RED_GRAPE_BUSH_PATCH_KEY = registerKey(
			"savanna_red_grape_bush");

	public static final ResourceKey<ConfiguredFeature<?, ?>> SAVANNA_WHITE_GRAPE_BUSH_PATCH_KEY = registerKey(
			"savanna_white_grape_bush");

	public static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_RED_GRAPE_BUSH_PATCH_KEY = registerKey(
			"jungle_red_grape_bush_patch");

	public static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_WHITE_GRAPE_BUSH_PATCH_KEY = registerKey(
			"jungle_white_grape_bush_patch");

	public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
		return ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new VineryIdentifier(name));
	}
}

