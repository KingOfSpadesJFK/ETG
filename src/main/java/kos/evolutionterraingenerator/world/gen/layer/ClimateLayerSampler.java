package kos.evolutionterraingenerator.world.gen.layer;

import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;

public class ClimateLayerSampler extends TerrainLayerSampler
{
	private double percision;
	public ClimateLayerSampler(int percision, LayerFactory<CachingLayerSampler> layerFactory) {
		super(layerFactory);
		this.percision = (double)percision;
	}
	
	public double sampleDouble(int x, int y) {
		return (double)sample(x, y) / Math.pow(10.0, percision);
	}
}
