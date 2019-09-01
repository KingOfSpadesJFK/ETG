package kos.evolutionterraingenerator;

import kos.evolutionterraingenerator.world.EvoType;
import kos.evolutionterraingenerator.world.biome.EvoBiomes;
import kos.evolutionterraingenerator.world.biome.BiomeHandler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHills;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EvolutionTerrainGenerator.MODID, version = EvolutionTerrainGenerator.VERSION)
public class EvolutionTerrainGenerator {
	
	public static final String MODID = "evolutionterraingenerator";
	public static final String VERSION = "0.1.0";
	
	@Instance(MODID)
	public static EvolutionTerrainGenerator instance;

	//Event Loaders
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		EvoType.register();
		//NewBiomes.initBiomes();
		MinecraftForge.EVENT_BUS.register(new BiomeHandler());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
	}

	@Mod.EventHandler
	public void init(FMLPostInitializationEvent event)
	{
		EvoBiomes.init();
	}
}
