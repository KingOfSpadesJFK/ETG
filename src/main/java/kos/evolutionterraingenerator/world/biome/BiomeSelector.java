package kos.evolutionterraingenerator.world.biome;

import kos.evolutionterraingenerator.world.biome.container.BiomeContainer;

public interface BiomeSelector 
{
	public void add(BiomeContainer bc);
	public BiomeContainer pick(double temperature, double humidity, double weirdness);
}
