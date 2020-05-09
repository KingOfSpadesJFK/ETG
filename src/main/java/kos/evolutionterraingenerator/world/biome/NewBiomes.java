package kos.evolutionterraingenerator.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import kos.evolutionterraingenerator.EvolutionTerrainGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(EvolutionTerrainGenerator.MODID)
public class NewBiomes {
	public static final Biome SNOWY_GIANT_TREE_TAIGA = register("snowy_giant_tree_taiga", new SnowyGiantTreeTaigaBiome(), false, 
			BiomeDictionary.Type.CONIFEROUS, 
			BiomeDictionary.Type.FOREST,
			BiomeDictionary.Type.SNOWY, 
			BiomeDictionary.Type.OVERWORLD);
	public static final Biome SNOWY_GIANT_SPRUCE_TAIGA = register("snowy_giant_spruce_taiga", new SnowyGiantSpruceTaiga(), false,
			BiomeDictionary.Type.CONIFEROUS, 
			BiomeDictionary.Type.FOREST,
			BiomeDictionary.Type.SNOWY,
			BiomeDictionary.Type.RARE, 
			BiomeDictionary.Type.OVERWORLD);

	public static final Biome GRAVEL_BEACH = register("gravel_beach", new GravelBeachBiome(), true,
			BiomeDictionary.Type.BEACH);
	public static final Biome SNOWY_GRAVEL_BEACH = register("snowy_gravel_beach", new SnowyGravelBeachBiome(), false, 
			BiomeDictionary.Type.BEACH,
			BiomeDictionary.Type.SNOWY, 
			BiomeDictionary.Type.OVERWORLD);

	public static final Biome DRY_BEACH = register("dry_beach", new DryBeach(), false,
			BiomeDictionary.Type.BEACH, 
			BiomeDictionary.Type.HOT, 
			BiomeDictionary.Type.DRY, 
			BiomeDictionary.Type.SANDY, 
			BiomeDictionary.Type.OVERWORLD);
	public static final Biome DRY_GRAVEL_BEACH = register("dry_gravel_beach", new DryGravelBeach(), false, 
			BiomeDictionary.Type.BEACH, 
			BiomeDictionary.Type.HOT, 
			BiomeDictionary.Type.DRY, 
			BiomeDictionary.Type.OVERWORLD);
	
	public static final Biome TUNDRA = register("tundra", new TundraBiome(), true, 
			BiomeDictionary.Type.COLD, 
			BiomeDictionary.Type.MOUNTAIN, 
			BiomeDictionary.Type.OVERWORLD);
	public static final Biome TUNDRA_WOODED = register("tundra_wooded", new TundraWooded(), true, 
			BiomeDictionary.Type.COLD, 
			BiomeDictionary.Type.MOUNTAIN, 
			BiomeDictionary.Type.OVERWORLD);
	public static final Biome GRAVELLY_TUNDRA = register("gravelly_tundra", new GravellyTundra(), false, 
			BiomeDictionary.Type.COLD, 
			BiomeDictionary.Type.MOUNTAIN, 
			BiomeDictionary.Type.RARE, 
			BiomeDictionary.Type.OVERWORLD);
	
	public static final Biome RAINFOREST = register("rainforest", new RainforestBiome(), false, 
			BiomeDictionary.Type.LUSH, 
			BiomeDictionary.Type.DENSE, 
			BiomeDictionary.Type.FOREST, 
			BiomeDictionary.Type.OVERWORLD);
	
	public static final Biome[] NEW_BIOMES = {
			NewBiomes.SNOWY_GIANT_TREE_TAIGA, 
			NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA, 
			NewBiomes.GRAVEL_BEACH, 
			NewBiomes.SNOWY_GRAVEL_BEACH, 
			NewBiomes.TUNDRA, 
			NewBiomes.TUNDRA_WOODED, 
			NewBiomes.GRAVELLY_TUNDRA,
			NewBiomes.RAINFOREST,
			NewBiomes.DRY_BEACH,
			NewBiomes.DRY_GRAVEL_BEACH};

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
