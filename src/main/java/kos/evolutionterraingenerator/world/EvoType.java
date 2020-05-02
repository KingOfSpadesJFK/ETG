package kos.evolutionterraingenerator.world;

import kos.evolutionterraingenerator.config.Config;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
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
			EvoBiomeProviderSettings that = new EvoBiomeProviderSettings();
			that.setWorldInfo(world.getWorldInfo());
			that.setGeneratorSettings(settings);
			that.setUseBOPBiomes(Config.enableBOP);
			return new EvoChunkGenerator(world, new EvoBiomeProvider(that, world), settings);
		}
        return world.dimension.createChunkGenerator();
    }
}
