package kos.evolutionterraingenerator.world.biome.selector;

import kos.evolutionterraingenerator.util.KdTree;
import kos.evolutionterraingenerator.world.biome.container.BiomeContainer;

public class SwampBiomeSelector implements BiomeSelector
{
	private KdTree<BiomeContainer> biomeTree;
	
	public SwampBiomeSelector() {
		this.biomeTree = new KdTree<BiomeContainer>();
	}

	@Override
	public void add(BiomeContainer bc) {
		double temp = bc.getTemperature();
		double weird = bc.getWeirdness();
		double weirdRange = bc.getWeirdness();
		biomeTree.insert(temp, weird, weirdRange, bc);
	}

	@Override
	public BiomeContainer pick(double temperature, double humidity, double weirdness) {
		return biomeTree.nearest(temperature, weirdness);
	}
}
