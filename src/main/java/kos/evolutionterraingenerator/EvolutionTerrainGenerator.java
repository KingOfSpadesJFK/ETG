package kos.evolutionterraingenerator;

import kos.evolutionterraingenerator.world.EvoType;
import kos.evolutionterraingenerator.world.biome.*;
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
import net.minecraftforge.registries.ObjectHolder;

@Mod(value = "evolutionterraingenerator")
public class EvolutionTerrainGenerator {
	
	public static final String MODID = "evolutionterraingenerator";
	
	public EvolutionTerrainGenerator()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBiomes);
        MinecraftForge.EVENT_BUS.register(this);
	}

	//Event Loaders
	private void setup(final FMLCommonSetupEvent event)
	{
		EvoType.register();
	}
	
	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> event)
	{
		event.getRegistry().registerAll(NewBiomes.NEW_BIOMES);
		EvoBiomes.init();
	}
}