package kos.evolutionterraingenerator.core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class EvoRegistry
{
	private static final String MODID = EvolutionTerrainGenerator.MODID;
	private static final String path = "worldge/biome/";
	
	public static ResourceLocation GRAVEL_BEACH;

	public static void init() 
	{
		GRAVEL_BEACH = new ResourceLocation(path + "gravel_beach");
	}
}
