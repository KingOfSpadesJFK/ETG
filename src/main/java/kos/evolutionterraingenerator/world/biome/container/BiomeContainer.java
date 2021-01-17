package kos.evolutionterraingenerator.world.biome.container;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BiomeContainer
{
	public Identifier mainBiome;
	public double temperature;
	public double humidity;
	public double weirdness;
	
	public BiomeContainer(Identifier mainBiome, double temperature, double humidity, double weirdness)
	{
		this.mainBiome = mainBiome;
		this.temperature = temperature;
		this.humidity = humidity;
		this.weirdness = weirdness;
	}
	
	public Identifier getID() {
		return mainBiome;
	}
	
	public final Biome getBiome(Registry<Biome> registry) {
		return registry.get(getID());
	}
}