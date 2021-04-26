package kos.evolutionterraingenerator.world.biome.create;

import com.google.common.collect.ImmutableList;

import kos.evolutionterraingenerator.core.EvolutionTerrainGenerator;
import kos.evolutionterraingenerator.world.biome.BiomeList;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.decorator.CountExtraDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class BiomeHandler 
{	
	private static final String MODID = EvolutionTerrainGenerator.MODID;

	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GRAVELLY = SurfaceBuilder.DEFAULT
			    .withConfig(SurfaceBuilder.GRAVEL_CONFIG);
	
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK_BUSH = Feature.TREE.configure(
			(new TreeFeatureConfig.Builder(
					new SimpleBlockStateProvider(Blocks.OAK_LOG.getDefaultState()), 
					new SimpleBlockStateProvider(Blocks.OAK_LEAVES.getDefaultState()), 
					new BushFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(1), 2),
					new StraightTrunkPlacer(1, 0, 0),
					new TwoLayersFeatureSize(0, 0, 0))
					).heightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)
			.build());
	public static final ConfiguredFeature<?, ?> RAINFOREST_TREES = Feature.RANDOM_SELECTOR.configure(
			new RandomFeatureConfig(
					ImmutableList.of(
							OAK_BUSH.withChance(0.35F),
							ConfiguredFeatures.DARK_OAK.withChance(0.1F),
							ConfiguredFeatures.FANCY_OAK_BEES_0002.withChance(0.45F),
							ConfiguredFeatures.OAK_BEES_0002.withChance(0.15F)),
					ConfiguredFeatures.FANCY_OAK_BEES_0002))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)));
	public static final ConfiguredFeature<?, ?> SPRING_LAVA_MORE = Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.LAVA_SPRING_CONFIG)
			.decorate(Decorator.RANGE_VERY_BIASED.configure(new RangeDecoratorConfig(8, 16, 256)))
			.spreadHorizontally()
			.repeat(40);
	
	private static void register(Identifier id, Biome b) {
		Registry.register(BuiltinRegistries.BIOME, id, b);
	}
	
	private static void register(String id, ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder) {
		Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier(MODID, id), surfaceBuilder);
	}
	
	private static <FC extends FeatureConfig> void register(String id, ConfiguredFeature<FC, ?> configuredFeature) {
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MODID, id), configuredFeature);
	}
	
	public static void registerAll()
	{
		//Surface Builders
		register("gravelly", GRAVELLY);
		
		//Features
		register("oak_bush", OAK_BUSH);
		register("rainforest_trees", RAINFOREST_TREES);
		register("spring_lava_more", SPRING_LAVA_MORE);
		
		//Biomes
		register(BiomeList.GRAVEL_BEACH, createBeach(0.0F, 0.025F, 0.8F, 0.4F, 4159204, 0, true));
		register(BiomeList.SNOWY_GRAVEL_BEACH, createBeach(0.0F, 0.025F, 0.0F, 0.4F, 4159204, -1, true));
		register(BiomeList.DRY_GRAVEL_BEACH, createBeach(0.0F, 0.025F, 2.0F, 0.0F, 4159204, 1, true));
		register(BiomeList.DRY_BEACH, createBeach(0.0F, 0.025F, 2.0F, 0.0F, 4159204, 1, false));

		register(BiomeList.SNOWY_GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.0F, false));
		register(BiomeList.SNOWY_GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.0F, true));
		
		register(BiomeList.RAINFOREST, createRainforest(0.5F, 0.5F));
		register(BiomeList.VOLCANO, createVolcano(true));
		register(BiomeList.VOLCANIC_EDGE, createVolcano(false));
	}
	
	public static Biome createBeach(float depth, float scale, float temperature, float downfall, int waterColor, int weather, boolean gravelly) 
	{
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		if (weather == 0) {
			builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.TURTLE, 5, 2, 5));
		}
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = (new GenerationSettings.Builder()).surfaceBuilder(gravelly ? GRAVELLY : ConfiguredSurfaceBuilders.DESERT);
		builder2.structureFeature(ConfiguredStructureFeatures.MINESHAFT);
		builder2.structureFeature(ConfiguredStructureFeatures.BURIED_TREASURE);
		builder2.structureFeature(ConfiguredStructureFeatures.SHIPWRECK_BEACHED);

		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return (new Biome.Builder())
				.precipitation(weather == 0 ? Biome.Precipitation.RAIN : (weather == -1 ? Biome.Precipitation.SNOW : Biome.Precipitation.NONE))
				.category(Biome.Category.BEACH)
				.depth(depth)
				.scale(scale)
				.temperature(temperature)
				.downfall(downfall)
				.effects((new BiomeEffects.Builder())
						.waterColor(waterColor)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(DefaultBiomeCreator.getSkyColor(temperature))
						.moodSound(BiomeMoodSound.CAVE).build())
				.spawnSettings(builder.build())
				.generationSettings(builder2.build())
				.build();
	}

	public static Biome createVolcano(boolean lava) {
		SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(spawnBuilder);
		GenerationSettings.Builder builder = (new GenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.BASALT_DELTAS)
				.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER)
				.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE);
		if (lava) {
			builder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.DELTA);
			builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, SPRING_LAVA_MORE);
		}
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BLACKSTONE_BLOBS);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_DELTA);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
		builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
		builder.structureFeature(ConfiguredStructureFeatures.MINESHAFT);
		builder.structureFeature(ConfiguredStructureFeatures.BURIED_TREASURE);
		DefaultBiomeFeatures.addDungeons(builder);
		DefaultBiomeFeatures.addMineables(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		
		return (new Biome.Builder())
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects((new BiomeEffects.Builder())
						.waterColor(0x757575)
						.grassColor(0x282828)
						.foliageColor(0x565656)
						.waterFogColor(0x373737)
						.fogColor(0x5c4576)
						.skyColor(DefaultBiomeCreator.getSkyColor(2.0F))
						.moodSound(BiomeMoodSound.CAVE)
						.build())
				.spawnSettings(spawnBuilder.build())
				.generationSettings(builder.build())
				.build();
	}

	public static Biome createRainforest(float depth, float scale) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.playerSpawnFriendly();
		GenerationSettings.Builder builder2 = (new GenerationSettings.Builder()).surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_JUNGLE);
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, RAINFOREST_TREES);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		builder2.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
		builder2.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
		builder2.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
		builder2.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);

		DefaultBiomeFeatures.addExtraDefaultFlowers(builder2);
		DefaultBiomeFeatures.addJungleGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addJungleVegetation(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return (new Biome.Builder())
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.FOREST)
				.depth(0.2F)
				.scale(0.35F)
				.temperature(0.825F)
				.downfall(0.85F)
				.effects((new BiomeEffects.Builder())
						.waterColor(0x58d152)
						.waterFogColor(0x41a96a)
						.fogColor(12638463)
						.skyColor(DefaultBiomeCreator.getSkyColor(1.0F))
						.moodSound(BiomeMoodSound.CAVE)
						.build())
				.spawnSettings(builder.build())
				.generationSettings(builder2.build())
				.build();
		
	}
}
