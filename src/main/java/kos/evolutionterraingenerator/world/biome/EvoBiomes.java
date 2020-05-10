package kos.evolutionterraingenerator.world.biome;

import java.util.ArrayList;

import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;

public class EvoBiomes 
{
	public static final EvoBiome PLAINS = new EvoBiome(Biomes.PLAINS, new Biome[]
			{Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS});
	public static final EvoBiome FOREST = new EvoBiome(Biomes.FOREST, new Biome[]
			{Biomes.FOREST, Biomes.FLOWER_FOREST});
	public static final EvoBiome BIRCH_FOREST = new EvoBiome(Biomes.BIRCH_FOREST, new Biome[]
			{Biomes.BIRCH_FOREST, Biomes.TALL_BIRCH_FOREST});
	public static final EvoBiome SNOWY_TUNDRA = new EvoBiome(Biomes.SNOWY_TUNDRA, 0.125F, new Biome[]
			{Biomes.SNOWY_TUNDRA, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_TUNDRA, Biomes.ICE_SPIKES});
	public static final EvoBiome MOUNTAINS = new EvoBiome(Biomes.MOUNTAINS, new Biome[]
			{Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS});
	public static final EvoBiome WOODED_MOUNTAINS = new EvoBiome(Biomes.WOODED_MOUNTAINS);
	public static final EvoBiome TAIGA = new EvoBiome(Biomes.TAIGA);
	public static final EvoBiome SNOWY_TAIGA = new EvoBiome(Biomes.SNOWY_TAIGA);
	public static final EvoBiome GIANT_TREE_TAIGA = new EvoBiome(Biomes.DARK_FOREST, new Biome[]
			{Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA});
	public static final EvoBiome DARK_FOREST = new EvoBiome(Biomes.DARK_FOREST);
	public static final EvoBiome JUNGLE = new EvoBiome(Biomes.JUNGLE, new Biome[]
			{Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE});
	public static final EvoBiome SAVANNA = new EvoBiome(Biomes.SAVANNA, new Biome[]
			 {Biomes.SAVANNA, Biomes.SHATTERED_SAVANNA});
	public static final EvoBiome DESERT = new EvoBiome(Biomes.DESERT, new Biome[]
			{Biomes.BADLANDS, Biomes.DESERT, Biomes.DESERT, Biomes.BADLANDS});
	public static final EvoBiome SONWY_GIANT_TREE_TAIGA = new EvoBiome(NewBiomes.SNOWY_GIANT_TREE_TAIGA, new Biome[]
			{NewBiomes.SNOWY_GIANT_TREE_TAIGA, NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA});
	public static final EvoBiome TUNDRA = new EvoBiome(NewBiomes.TUNDRA, new Biome[]
			{NewBiomes.TUNDRA, NewBiomes.GRAVELLY_TUNDRA});
	public static final EvoBiome WOODED_TUNDRA = new EvoBiome(NewBiomes.TUNDRA_WOODED);
	public static final EvoBiome RAINFOREST_ROOFED = new EvoBiome(NewBiomes.RAINFOREST, new Biome[]
			{	NewBiomes.RAINFOREST, Biomes.DARK_FOREST, 
				NewBiomes.RAINFOREST, Biomes.DARK_FOREST, 
				NewBiomes.RAINFOREST, Biomes.DARK_FOREST, 
				NewBiomes.RAINFOREST, Biomes.DARK_FOREST	});
	public static final EvoBiome RAINFOREST = new EvoBiome(NewBiomes.RAINFOREST);
	public static final EvoBiome SAVANNA_PLAINS = new EvoBiome(Biomes.PLAINS, new Biome[]
			{	Biomes.SAVANNA, Biomes.PLAINS, 
					Biomes.SAVANNA, Biomes.PLAINS, 
					Biomes.SAVANNA, Biomes.PLAINS, 
					Biomes.SAVANNA, Biomes.PLAINS,
					Biomes.SHATTERED_SAVANNA, Biomes.SUNFLOWER_PLAINS});
	
	public static EvoBiome HOT_SWAMP = new EvoBiome(Biomes.SWAMP);
	public static EvoBiome WARM_SWAMP = new EvoBiome(Biomes.SWAMP);
	public static EvoBiome COLD_SWAMP = new EvoBiome(Biomes.SWAMP);
	
	public static EvoBiome ISLAND_BIOMES = new EvoBiome(Biomes.PLAINS, new Biome[]
			{
				Biomes.PLAINS,
				Biomes.FOREST,
				Biomes.JUNGLE,
				Biomes.PLAINS,
				Biomes.FOREST,
				Biomes.JUNGLE,
			});

	public static final ArrayList<EvoBiome> SNOWY_BIOMES = new ArrayList<EvoBiome>();
	public static final ArrayList<EvoBiome> COLD_BIOMES = new ArrayList<EvoBiome>();
	public static final ArrayList<EvoBiome> WARM_BIOMES = new ArrayList<EvoBiome>();
    public static final ArrayList<EvoBiome> HOT_BIOMES = new ArrayList<EvoBiome>();
    public static final ArrayList<EvoBiome> ARID_BIOMES = new ArrayList<EvoBiome>();
    
	public static void init()
	{
		SNOWY_BIOMES.clear();
		SNOWY_BIOMES.add(SNOWY_TUNDRA);
		SNOWY_BIOMES.add(SNOWY_TAIGA);
		SNOWY_BIOMES.add(SONWY_GIANT_TREE_TAIGA);
		SNOWY_BIOMES.sort(null);

		COLD_BIOMES.clear();;
		COLD_BIOMES.add(MOUNTAINS);
		COLD_BIOMES.add(WOODED_MOUNTAINS);
		COLD_BIOMES.add(TAIGA);
		COLD_BIOMES.add(GIANT_TREE_TAIGA);
		COLD_BIOMES.sort(null);

		WARM_BIOMES.clear();;
		WARM_BIOMES.add(PLAINS);
		WARM_BIOMES.add(BIRCH_FOREST);
		WARM_BIOMES.add(FOREST);
		WARM_BIOMES.add(DARK_FOREST);
		WARM_BIOMES.sort(null);

		HOT_BIOMES.clear();
		HOT_BIOMES.add(SAVANNA);
		HOT_BIOMES.add(PLAINS);
		HOT_BIOMES.add(FOREST);
		ARID_BIOMES.add(RAINFOREST_ROOFED);
		HOT_BIOMES.sort(null);

		ARID_BIOMES.clear();
		ARID_BIOMES.add(DESERT);
		ARID_BIOMES.add(SAVANNA);
		ARID_BIOMES.add(RAINFOREST_ROOFED);
		ARID_BIOMES.add(JUNGLE);
		ARID_BIOMES.sort(null);
		
		HOT_SWAMP = new EvoBiome(Biomes.SWAMP);
		WARM_SWAMP = new EvoBiome(Biomes.SWAMP);
		COLD_SWAMP = new EvoBiome(Biomes.SWAMP);
	}
	
	public static EvoBiome[] toArray(ArrayList<EvoBiome> list)
	{
		EvoBiome[] arr = new EvoBiome[list.size()];
		int i = 0;
		for (EvoBiome b : list)
		{
			arr[i] = b;
			i++;
		}
		System.out.println();
		return arr;
	}
}
