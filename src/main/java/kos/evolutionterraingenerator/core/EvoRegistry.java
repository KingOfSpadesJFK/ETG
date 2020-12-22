package kos.evolutionterraingenerator.core;

import kos.evolutionterraingenerator.world.biome.EvoBiomeMaker;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class EvoRegistry
{
	private static final String MODID = EvolutionTerrainGenerator.MODID;
	
	public static RegistryObject<Biome> GRAVEL_BEACH;
	
	private static RegistryObject<Biome> register(String id, IForgeRegistry<Biome> registry, Biome biome)
	{
		ResourceLocation thing = new ResourceLocation(MODID, id);
		biome.setRegistryName(thing);
		registry.register(biome);
		return RegistryObject.of(thing, ForgeRegistries.BIOMES);
	}
	
	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> event) 
	{
		GRAVEL_BEACH = register("gravel_beach", event.getRegistry(), EvoBiomeMaker.createGravelBeachBiome(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false));
	}
}
