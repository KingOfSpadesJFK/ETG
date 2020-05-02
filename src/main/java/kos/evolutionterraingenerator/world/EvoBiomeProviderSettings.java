package kos.evolutionterraingenerator.world;

import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.storage.WorldInfo;

public class EvoBiomeProviderSettings extends OverworldBiomeProviderSettings
{
	private WorldInfo worldInfo;
	private EvoGenSettings settings;
	private boolean useBOPBiomes;
	
	public OverworldBiomeProviderSettings setWorldInfo(WorldInfo worldInfo)
	{
		this.worldInfo = worldInfo;
		return this;
	}
	public OverworldBiomeProviderSettings setGeneratorSettings(EvoGenSettings settings) 
	{
		this.settings = settings;
		return this;
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
