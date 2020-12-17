package kos.evolutionterraingenerator;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::config);
        MinecraftForge.EVENT_BUS.register(this);
        
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	//Event Loaders
	private void setup(final FMLCommonSetupEvent event)
	{
	}
}