package kos.evolutionterraingenerator.world.biome;

import kos.evolutionterraingenerator.core.EvolutionTerrainGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class BiomeHandler 
{
	private static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GRAVELLY = SurfaceBuilder.DEFAULT
			    .withConfig(new TernarySurfaceConfig(
			      Blocks.GRAVEL.getDefaultState(),
			      Blocks.GRAVEL.getDefaultState(),
			      Blocks.GRAVEL.getDefaultState()));
	private static final String MODID = EvolutionTerrainGenerator.MODID;
	
	private static void register(Identifier id, Biome b) {
		Registry.register(BuiltinRegistries.BIOME, id, b);
	}
	
	private static void register(Identifier id, ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder) {
		Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, id, surfaceBuilder);
	}
	
	public static void registerAll()
	{
		register(new Identifier(MODID, "gravelly"), GRAVELLY);
		
		register(BiomeList.GRAVEL_BEACH, createBeach(0.0F, 0.025F, 0.8F, 0.4F, 4159204, 0, true));
		register(BiomeList.SNOWY_GRAVEL_BEACH, createBeach(0.0F, 0.025F, 0.0F, 0.4F, 4159204, -1, true));
		register(BiomeList.DRY_GRAVEL_BEACH, createBeach(0.0F, 0.025F, 2.0F, 0.0F, 4159204, 1, true));
		register(BiomeList.DRY_BEACH, createBeach(0.0F, 0.025F, 2.0F, 0.0F, 4159204, 1, false));

		register(BiomeList.SNOWY_GIANT_TREE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.0F, false));
		register(BiomeList.SNOWY_GIANT_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.0F, true));
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
}
