package kos.evolutionterraingenerator.world.biome;

import net.minecraft.world.biome.Biome;

public class EvoBiome 
{
	private Biome defaultBiome;
	private Biome[] similarBiomes;		//int array of IDs of biomes similar to this
	private float downfall;
	
	public EvoBiome(Biome defaultBiome, float downfall, Biome[] similarBiomes)
	{
		this.defaultBiome = defaultBiome;
		this.similarBiomes = similarBiomes;
		this.downfall = downfall;
	}
	
	public EvoBiome(Biome defaultBiome, Biome[] similarBiomes)
	{
		this(defaultBiome, defaultBiome.getDownfall(), similarBiomes);
	}
	
	public EvoBiome(Biome biome)
	{
		this(biome, biome.getDownfall(), null);
	}

	public EvoBiome(Biome biome, float f)
	{
		this(biome, f, null);
	}

	public Biome getBiome(double chance)
	{
		if (similarBiomes == null || similarBiomes.length == 0)
			return defaultBiome;
		
		return similarBiomes[(int)((similarBiomes.length - 1) * chance)];
	}
	
	public Biome getDefaultBiome()
	{
		return defaultBiome;
	}

	public int compareTo(EvoBiome evoBiome)
	{
		if (this.downfall < evoBiome.downfall)
			return -1;
		if (this.downfall > evoBiome.downfall)
			return 1;
		return 0;
	}
}
