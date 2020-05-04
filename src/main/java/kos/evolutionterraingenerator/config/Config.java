package kos.evolutionterraingenerator.config;

import kos.evolutionterraingenerator.EvolutionTerrainGenerator;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final BiomeConfig BIOME_CONFIG = new BiomeConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
	public static void bake()
	{
		EvolutionTerrainGenerator.logger.debug("Loading ETG Configurations...");
		EvolutionTerrainGenerator.logger.debug("ETG configured!");
	}
	
	public static class BiomeConfig
	{
		public BiomeConfig(ForgeConfigSpec.Builder builder)
		{
		}
	}
}
