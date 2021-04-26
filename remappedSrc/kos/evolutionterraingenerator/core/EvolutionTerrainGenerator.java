package kos.evolutionterraingenerator.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kos.evolutionterraingenerator.world.EvoGeneratorType;
import kos.evolutionterraingenerator.world.biome.EvoBiomeSource;
import kos.evolutionterraingenerator.world.biome.create.BiomeHandler;
import kos.evolutionterraingenerator.world.gen.EvoChunkGenerator;
import kos.evolutionterraingenerator.world.gen.EvoGenSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
public class EvolutionTerrainGenerator implements ModInitializer  {
	
	public static final String MODID = "evolutionterraingenerator";
	public static final String VERSION = "0.2.0";
	
	public static EvolutionTerrainGenerator instance;

    public static Logger logger = LogManager.getLogger(MODID);

	@Override
	public void onInitialize() {
		instance = this;
		BiomeHandler.registerAll();
		EvoChunkGenerator.register();
		EvoBiomeSource.register();
        EvoGenSettings.register();

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			EvoGeneratorType.register();
		}
	}
}