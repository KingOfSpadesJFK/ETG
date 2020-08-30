package kos.evolutionterraingenerator.world;

import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.storage.WorldInfo;

public class EvoBiomeProviderSettings extends OverworldBiomeProviderSettings
{
	private WorldInfo worldInfo;
	private EvoGenSettings settings;
	
	private boolean useBOPBiomes;
	private float biomeScale = 1.0f;
	private float humidScale = 1.0f;
	private float tempScale = 1.0f;
	private float oceanScale = 1.0f;
	private int seaLevel = 63;
	
	public void setup()
	{
		useBOPBiomes = worldInfo.getGeneratorOptions().getBoolean(EvoType.USE_BOP_TAG);
	}
	
	public OverworldBiomeProviderSettings setWorldInfo(WorldInfo worldInfo)
	{
		this.worldInfo = worldInfo;
		return this;
	}

	public WorldInfo getWorldInfo()
	{
		return this.worldInfo;
	}
	
	public OverworldBiomeProviderSettings setGeneratorSettings(EvoGenSettings settings) 
	{
		this.settings = settings;
		return this;
	}
	
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
		// TODO Auto-generated method stub
		return seaLevel;
	}
}
