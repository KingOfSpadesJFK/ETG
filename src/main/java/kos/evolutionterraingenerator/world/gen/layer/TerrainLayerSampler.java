package kos.evolutionterraingenerator.world.gen.layer;

import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;

public class TerrainLayerSampler
{
	private final CachingLayerSampler sampler;

	public TerrainLayerSampler(LayerFactory<CachingLayerSampler> layerFactory) {
		this.sampler = (CachingLayerSampler)layerFactory.make();
	}
	
	public int sample(int x, int z)	{
		int i = this.sampler.sample(x, z);
		return i;
	}
	
	public static boolean isPlateau(int value)	{
		return value == PLATEAU_LAYER;
	}
	
	public static final int PLAINS_LAYER = 0;
	public static final int MOUNTAINS_LAYER = 1;
	public static final int PLATEAU_LAYER = 2;
	public static final int RIVER_LAYER = 3;

	public static final int PLATEAU_STEPPE_COUNT = 4;
}
