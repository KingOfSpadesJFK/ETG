package kos.evolutionterraingenerator.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
	public static class GeneralConfig
	{
		public final ForgeConfigSpec.ConfigValue<Boolean> modEnabled;
		
		public GeneralConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("General Options");
			modEnabled = builder.comment("Enables ETG mod on servers. SETTING THIS ON SERVER.PROPERTIES WILL NOT WORK!!!")
					.define("enableMod", true);
		}
	}
}
