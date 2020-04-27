package kos.evolutionterraingenerator;

import kos.evolutionterraingenerator.world.EvoType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EvolutionTerrainGenerator.MODID)
public class EvolutionTerrainGenerator {
	
	public static final String MODID = "evolutionterraingenerator";
	public static final String VERSION = "0.2.0";
	
	public EvolutionTerrainGenerator()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
	}

	//Event Loaders
	private void setup(final FMLCommonSetupEvent event)
	{
		EvoType.register();
	}
}