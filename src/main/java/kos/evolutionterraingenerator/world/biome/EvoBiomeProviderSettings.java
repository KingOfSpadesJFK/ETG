package kos.evolutionterraingenerator.world.biome;

import net.minecraft.world.World;

public class EvoBiomeProviderSettings
{

	private World world;
	private String genSettings;
	
	private boolean useBOPBiomes;
	private float biomeScale = 1.0f;
	private float humidScale = 1.0f;
	private float tempScale = 1.0f;
	private float oceanScale = 1.0f;
	private int seaLevel = 63;

	public EvoBiomeProviderSettings(World world) 
	{
		this.world = world;
	}
	
	public EvoBiomeProviderSettings(String generatorSettings) {
		this.genSettings = generatorSettings;
	}

	public EvoBiomeProviderSettings() {	setup(); }

	public void setup()
	{
		useBOPBiomes = false;
	}
	
	public void setWorldInfo(World world)
	{
		this.world = world;
	}

	public World getWorldInfo()
	{
		return this.world;
	}
	
	public void setGeneratorSettings(String settings) 
	{
		this.genSettings = settings;
	}
	
	public void setUseBOPBiomes(boolean useBOPBiomes)
	{
		this.useBOPBiomes = useBOPBiomes;
	}
	
	public String getSettings()
	{
		return genSettings;
	}
	
	public boolean isUseBOPBiomes() 
	{
		return useBOPBiomes;
	}

	public float getBiomeScale()
	{
		return biomeScale;
	}

	public float getHumidityScale()
	{
		return humidScale;
	}

	public float getTemperatureScale()
	{
		return tempScale;
	}

	public float getOceanScale()
	{
		return oceanScale;
	}

	public int getSeaLevel() 
	{
		return seaLevel;
	}
}
