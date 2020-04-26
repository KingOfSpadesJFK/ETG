package kos.evolutionterraingenerator.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;

public class EvoType extends WorldType
{    
	public static void register()
	{
		new EvoType("EVOLUTION");
	}
	
	private EvoType(String name)
	{
		super(name);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public ChunkGenerator<?> createChunkGenerator(World world)
    {
		ChunkGenerator<?> generator;
		EvoGenSettings settings = new EvoGenSettings();
		if (world.dimension.getType() == DimensionType.OVERWORLD)
		{
			return new EvoChunkGenerator(world, new BiomeProviderEvo(new OverworldBiomeProviderSettings(world.getWorldInfo()), world), settings);
		}
        return world.dimension.createChunkGenerator();
    }
}
