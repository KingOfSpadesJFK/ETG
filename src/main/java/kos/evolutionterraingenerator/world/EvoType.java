package kos.evolutionterraingenerator.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;

public class EvoType extends WorldType
{
	public EvoType(String name)
	{
		super(name);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public ChunkGenerator<?> createChunkGenerator(World world)
    {
		EvoGenSettings settings = new EvoGenSettings();
		if (world.dimension.getType() == DimensionType.OVERWORLD)
		{
			OverworldBiomeProviderSettings that = new OverworldBiomeProviderSettings();
			that.setWorldInfo(world.getWorldInfo());
			that.setGeneratorSettings(settings);
			return new EvoChunkGenerator(world, new EvoBiomeProvider(that, world), settings);
		}
        return world.dimension.createChunkGenerator();
    }
}
