package kos.evolutionterraingenerator.config;

import kos.evolutionterraingenerator.EvolutionTerrainGenerator;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig(BUILDER);
	public static final BiomeConfig BIOME_CONFIG = new BiomeConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
	public static boolean enableBOP;
	
	public static void bake()
	{
		EvolutionTerrainGenerator.logger.debug("Loading ETG Configurations...");
		enableBOP = BIOME_CONFIG.enableBOP.get();
	}
	
	public static class GeneralConfig
	{
		protected final ForgeConfigSpec.ConfigValue<Boolean> modEnabled;
		
		public GeneralConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("General Options");
			modEnabled = builder.comment("(NOT WORKING. SETTING THIS TO TRUE WILL DO NOTHING!!!) Enables ETG mod on servers. SETTING THIS ON SERVER.PROPERTIES WILL NOT WORK!!!")
					.define("enableMod", false);
			builder.pop();
		}
	}
	
	public static class BiomeConfig
	{
		protected final ForgeConfigSpec.ConfigValue<Boolean> enableBOP;
		
		public BiomeConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("Biome Generation Options");
			enableBOP = builder.comment("Enable support for Biomes O' Plenty, allowing BOP biomes to spawn in the ETG.")
					.define("enableBOP", true);
			builder.pop();
		}
	}
}
