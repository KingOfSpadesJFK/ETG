package kos.evolutionterraingenerator.world;

import net.minecraft.world.biome.provider.IBiomeProviderSettings;
import net.minecraft.world.storage.WorldInfo;

public class EvoBiomeProviderSettings implements IBiomeProviderSettings
{
	private WorldInfo worldInfo;
	private EvoGenSettings settings;
	private boolean useBOPBiomes;
	
	public void setWorldInfo(WorldInfo worldInfo)
	{
		this.worldInfo = worldInfo;
	}
	public void setGeneratorSettings(EvoGenSettings settings) 
	{
		this.settings = settings;
	}
	
	public EvoGenSettings getSettings()
	{
		return settings;
	}
	public WorldInfo getWorldInfo()
	{
		return worldInfo;
	}
	public boolean isUseBOPBiomes() {
		return useBOPBiomes;
	}
	public void setUseBOPBiomes(boolean useBOPBiomes) {
		this.useBOPBiomes = useBOPBiomes;
	}
}
