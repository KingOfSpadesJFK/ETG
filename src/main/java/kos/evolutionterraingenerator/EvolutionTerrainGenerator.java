package kos.evolutionterraingenerator;

import kos.evolutionterraingenerator.world.EvoType;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = "evolutionterraingenerator")
public class EvolutionTerrainGenerator {
	
	public static final String MODID = "evolutionterraingenerator";
	
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
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Biome> event)
	{
		
	}

	private void init(final FMLCommonSetupEvent event)
	{
		EvoBiomes.init();
	}
	
}
