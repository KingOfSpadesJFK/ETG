package kos.evolutionterraingenerator.core;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeSetup
{
	private static Biome register(String name, Biome biome, boolean spawn, BiomeDictionary.Type...types)
	{
		biome.setRegistryName(EvolutionTerrainGenerator.MODID, name);
		ForgeRegistries.BIOMES.register(biome);
		//BiomeDictionary.addTypes(biome, types);
		return biome;
	}
}
