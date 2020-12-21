package kos.evolutionterraingenerator.core;

import kos.evolutionterraingenerator.world.biome.EvoBiomeMaker;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EvoBiomeRegistry
{
	private static final String MODID = EvolutionTerrainGenerator.MODID;
	private static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
	
	public static final RegistryObject<Biome> GRAVEL_BEACH = register("gravel_beach", EvoBiomeMaker.createGravelBeachBiome(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false));
	
	public static void init()
	{
		BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private static RegistryObject<Biome> register(String id, Biome biome)
	{
		return BIOMES.register(id, () -> biome);
	}
}
