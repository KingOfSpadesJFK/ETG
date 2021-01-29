package kos.evolutionterraingenerator.world.biome.selector;

import kos.evolutionterraingenerator.util.KdTree;
import kos.evolutionterraingenerator.world.biome.container.BiomeContainer;

public class OceanBiomeSelector implements BiomeSelector
{
	private KdTree<BiomeContainer> shallowTree;
	private KdTree<BiomeContainer> deepTree;
	
	public OceanBiomeSelector() {
		this.shallowTree = new KdTree<BiomeContainer>();
		this.deepTree = new KdTree<BiomeContainer>();
	}

	@Override	
	public void add(BiomeContainer bc) {
		double temp = bc.getTemperature();
		double weird = bc.getWeirdness();
		double weirdRange = bc.getWeirdness();
		if (bc.getHumidity() >= 0.5) {
			this.deepTree.insert(temp, weird, weirdRange, bc);
		} else {;
			this.shallowTree.insert(temp, weird, weirdRange, bc);
		}
	}

	@Override
	public BiomeContainer pick(double temperature, double humidity, double weirdness) {
		if (humidity >= 0.5) {
			return deepTree.nearest(temperature, weirdness);
		} else {
			return shallowTree.nearest(temperature, weirdness);
		}
	}
}
