package kos.evolutionterraingenerator.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import kos.evolutionterraingenerator.EvolutionTerrainGenerator;
import kos.evolutionterraingenerator.world.BiomeProviderEvo;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

@ObjectHolder(EvolutionTerrainGenerator.MODID)
public class NewBiomes {
	public static final Biome SNOWY_GIANT_TREE_TAIGA = register("snowy_giant_tree_taiga", new SnowyGiantTreeTaigaBiome(), false, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST);
	public static final Biome SNOWY_GIANT_SPRUCE_TAIGA = register("snowy_giant_spruce_taiga", new SnowyGiantSpruceTaiga(), false, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.RARE);

	public static final Biome GRAVEL_BEACH = register("gravel_beach", new GravelBeachBiome(), true, BiomeDictionary.Type.BEACH);
	public static final Biome SNOWY_GRAVEL_BEACH = register("snowy_gravel_beach", new SnowyGravelBeachBiome(), false, BiomeDictionary.Type.BEACH);

	public static final Biome TUNDRA = register("tundra", new TundraBiome(), true, BiomeDictionary.Type.COLD);
	public static final Biome TUNDRA_WOODED = register("tundra_wooded", new TundraWooded(), true, BiomeDictionary.Type.COLD);
	public static final Biome GRAVELLY_TUNDRA = register("gravelly_tundra", new GravellyTundra(), false, BiomeDictionary.Type.COLD, BiomeDictionary.Type.RARE);
	
	public static final Biome RAINFOREST = register("rainforest", new RainforestBiome(), false, BiomeDictionary.Type.LUSH, BiomeDictionary.Type.FOREST);
	
	public static final Biome[] NEW_BIOMES = {
			NewBiomes.SNOWY_GIANT_TREE_TAIGA, 
			NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA, 
			NewBiomes.GRAVEL_BEACH, 
			NewBiomes.SNOWY_GRAVEL_BEACH, 
			NewBiomes.TUNDRA, 
			NewBiomes.TUNDRA_WOODED, 
			NewBiomes.GRAVELLY_TUNDRA,
			NewBiomes.RAINFOREST};

	private static Biome register(String name, Biome biome, boolean spawn, BiomeDictionary.Type...types)
	{
		biome.setRegistryName(EvolutionTerrainGenerator.MODID, name);
		ForgeRegistries.BIOMES.register(biome);
		BiomeDictionary.addTypes(biome, types);
		if (spawn)
			BiomeManager.addSpawnBiome(biome);
		return biome;
	}
}
