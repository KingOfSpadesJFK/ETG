package kos.evolutionterraingenerator.world.biome;

import net.minecraft.world.biome.Biome;

public class EvoBiome implements Comparable<EvoBiome>
{
	private Biome biome;
	private Biome[] similarBiomes;		//int array of IDs of biomes similar to this
	
	public EvoBiome(Biome biome, Biome[] similarBiomes) {
		this.biome = biome;
		this.similarBiomes = similarBiomes;
	}
	
	public Biome getBiome(double chance)
	{
		if (similarBiomes == null || similarBiomes.length == 0)
			return biome;
		
		return similarBiomes[(int)((similarBiomes.length - 1) * chance)];
	}
	
	public Biome getDefaultBiome()
	{
		return biome;
	}

	public int compareTo(EvoBiome evoBiome)
	{
		if (this.biome.getDownfall() < evoBiome.biome.getDownfall())
			return -1;
		if (this.biome.getDownfall() > evoBiome.biome.getDownfall())
			return 1;
		return 0;
	}
}