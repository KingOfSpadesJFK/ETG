package kos.evolutionterraingenerator;

import kos.evolutionterraingenerator.world.EvoType;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = EvolutionTerrainGenerator.MODID)
public class EvolutionTerrainGenerator {
	
	public static final String MODID = "evolutionterraingenerator";
	public static final String VERSION = "0.1.0";
	
	public static EvolutionTerrainGenerator instance;

    public static Logger logger = LogManager.getLogger(MODID);
	
	public EvolutionTerrainGenerator()
	{
		instance = this;
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
		EvoBiomes.init();
	}
	
	public void serverSetup(final FMLDedicatedServerSetupEvent event)
	{
        ServerProperties serverProperties = event.getServerSupplier().get().getServerProperties();
        logger.info("ETG is installed on this server. Current level type is " + serverProperties.worldType);
	}

	//Event Loaders
	private void setup(final FMLCommonSetupEvent event)
	{
		EvoType.register();
	}
}