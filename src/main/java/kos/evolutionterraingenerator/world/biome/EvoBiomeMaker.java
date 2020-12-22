package kos.evolutionterraingenerator.world.biome;

import kos.evolutionterraingenerator.core.EvolutionTerrainGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class EvoBiomeMaker 
{
	private static int getSkyColorWithTemperatureModifier(float temperature) 
	{
		float lvt_1_1_ = temperature / 3.0F;
		lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
		return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}  
	
	public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> gravel_surface = registerConfiguredSurfaceBuilder("etg_gravel", SurfaceBuilder.DEFAULT.func_242929_a(SurfaceBuilder.GRAVEL_CONFIG));
	
	private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> registerConfiguredSurfaceBuilder(String name, ConfiguredSurfaceBuilder<SC> surfaceBuilder) 
	{
		return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, name, surfaceBuilder);
	}
	   
	public static Biome createGravelBeachBiome(float depth, float scale, float temperature, float downfall, int waterColor, boolean isColdBiome) 
	{
		MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
		if (!isColdBiome) {
			mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.TURTLE, 5, 2, 5));
		}

		DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
		BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(gravel_surface);
		biomegenerationsettings$builder.withStructure(StructureFeatures.MINESHAFT);
		biomegenerationsettings$builder.withStructure(StructureFeatures.BURIED_TREASURE);
		biomegenerationsettings$builder.withStructure(StructureFeatures.SHIPWRECK_BEACHED);
		biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, 
				Feature.FOREST_ROCK
				.withConfiguration(new BlockStateFeatureConfig(Blocks.COBBLESTONE.getDefaultState()))
				.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).func_242732_c(2));

		biomegenerationsettings$builder.withStructure(StructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
		return (new Biome.Builder())
				.precipitation(isColdBiome ? Biome.RainType.SNOW : Biome.RainType.RAIN)
				.category(Biome.Category.BEACH)
				.depth(depth)
				.scale(scale)
				.temperature(temperature)
				.downfall(downfall)
				.setEffects((new BiomeAmbience.Builder())
						.setWaterColor(waterColor)
						.setWaterFogColor(329011)
						.setFogColor(12638463)
						.withSkyColor(getSkyColorWithTemperatureModifier(temperature))
						.setMoodSound(MoodSoundAmbience.DEFAULT_CAVE)
						.build())
				.withMobSpawnSettings(
						mobspawninfo$builder.copy())
				.withGenerationSettings(
						biomegenerationsettings$builder.build())
				.build();
	}
}
