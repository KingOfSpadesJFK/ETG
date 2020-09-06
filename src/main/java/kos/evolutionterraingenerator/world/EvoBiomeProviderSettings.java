package kos.evolutionterraingenerator.world;

public class EvoBiomeProviderSettings
{
	private EvoGenSettings settings;
	
	private boolean useBOPBiomes;
	private float biomeScale = 1.0f;
	private float humidScale = 1.0f;
	private float tempScale = 1.0f;
	private float oceanScale = 1.0f;
	private int seaLevel = 63;
	
	public void setUseBOPBiomes(boolean useBOPBiomes)
	{
		this.useBOPBiomes = useBOPBiomes;
	}
	
	public EvoGenSettings getSettings()
	{
		return settings;
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
