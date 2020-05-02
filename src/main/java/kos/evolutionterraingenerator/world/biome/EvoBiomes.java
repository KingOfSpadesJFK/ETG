package kos.evolutionterraingenerator.world.biome;

import java.util.ArrayList;

import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;

public class EvoBiomes 
{
	private static final Biome[] PlainsArr = {Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS};
	private static final Biome[] ForestArr = {Biomes.FOREST, Biomes.FLOWER_FOREST};
	private static final Biome[] BirchForestArr = {Biomes.BIRCH_FOREST, Biomes.TALL_BIRCH_FOREST};
	private static final Biome[] IcePlainsArr = {Biomes.SNOWY_TUNDRA, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_TUNDRA, Biomes.ICE_SPIKES};
	private static final Biome[] TundraArr = {NewBiomes.TUNDRA, NewBiomes.GRAVELLY_TUNDRA};
	private static final Biome[] RedwoodArr = {Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA};
	private static final Biome[] SavannaArr = {Biomes.SAVANNA, Biomes.SHATTERED_SAVANNA};
	private static final Biome[] DesertArr = {Biomes.DESERT, Biomes.DESERT, Biomes.BADLANDS};
	private static final Biome[] SnowyRedwoodArr = {NewBiomes.SNOWY_GIANT_TREE_TAIGA, NewBiomes.SNOWY_GIANT_SPRUCE_TAIGA};
	private static final Biome[] ExtremeArr = {Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS};
	private static final Biome[] JungleArr = {Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE};
	private static final Biome[] RainforestArr =
		{	NewBiomes.RAINFOREST, Biomes.DARK_FOREST, 
			NewBiomes.RAINFOREST, Biomes.DARK_FOREST, 
			NewBiomes.RAINFOREST, Biomes.DARK_FOREST, 
			NewBiomes.RAINFOREST, Biomes.DARK_FOREST	};
	private static final Biome[] SavannaPlainsArr =
		{	Biomes.SAVANNA, Biomes.PLAINS, 
			Biomes.SAVANNA, Biomes.PLAINS, 
			Biomes.SAVANNA, Biomes.PLAINS, 
			Biomes.SAVANNA, Biomes.PLAINS,
			Biomes.SHATTERED_SAVANNA, Biomes.SUNFLOWER_PLAINS};
	
	public static EvoBiome PLAINS = new EvoBiome(Biomes.PLAINS, PlainsArr);
	public static EvoBiome FOREST = new EvoBiome(Biomes.FOREST, ForestArr);
	public static EvoBiome BIRCH_FOREST = new EvoBiome(Biomes.BIRCH_FOREST, BirchForestArr);
	public static EvoBiome ICE_PLAINS = new EvoBiome(Biomes.SNOWY_TUNDRA, IcePlainsArr);
	public static EvoBiome EXTREME_HILLS = new EvoBiome(Biomes.MOUNTAINS, ExtremeArr);
	public static EvoBiome TAIGA = new EvoBiome(Biomes.TAIGA, null);
	public static EvoBiome COLD_TAIGA = new EvoBiome(Biomes.SNOWY_TAIGA, null);
	public static EvoBiome REDWOOD_TAIGA = new EvoBiome(Biomes.DARK_FOREST, RedwoodArr);
	public static EvoBiome ROOFED_FOREST = new EvoBiome(Biomes.DARK_FOREST, null);
	public static EvoBiome JUNGLE = new EvoBiome(Biomes.JUNGLE, JungleArr);
	public static EvoBiome SAVANNA = new EvoBiome(Biomes.SAVANNA, SavannaArr);
	public static EvoBiome DESERT = new EvoBiome(Biomes.DESERT, DesertArr);
	public static EvoBiome SONWY_GIANT_TREE_TAIGA = new EvoBiome(NewBiomes.SNOWY_GIANT_TREE_TAIGA, SnowyRedwoodArr);
	public static EvoBiome TUNDRA = new EvoBiome(NewBiomes.TUNDRA, TundraArr);
	public static EvoBiome WOODED_TUNDRA = new EvoBiome(NewBiomes.TUNDRA_WOODED, null);
	public static EvoBiome RAINFOREST_ROOFED = new EvoBiome(NewBiomes.RAINFOREST, RainforestArr);
	public static EvoBiome RAINFOREST = new EvoBiome(NewBiomes.RAINFOREST, null);
	public static EvoBiome SAVANNA_PLAINS = new EvoBiome(Biomes.PLAINS, SavannaPlainsArr);
	
	public static EvoBiome HOT_SWAMP = new EvoBiome(Biomes.SWAMP, null);
	public static EvoBiome WARM_SWAMP = new EvoBiome(Biomes.SWAMP, null);
	public static EvoBiome COLD_SWAMP = new EvoBiome(Biomes.SWAMP, null);

	public static ArrayList<EvoBiome> SNOWY_BIOMES;
	public static ArrayList<EvoBiome> COLD_BIOMES;
	public static ArrayList<EvoBiome> WARM_BIOMES;
    public static ArrayList<EvoBiome> HOT_BIOMES;
    public static ArrayList<EvoBiome> ARID_BIOMES;
    
	public static void init()
	{
		SNOWY_BIOMES = new ArrayList<EvoBiome>();
		SNOWY_BIOMES.add(ICE_PLAINS);
		SNOWY_BIOMES.add(COLD_TAIGA);
		SNOWY_BIOMES.add(SONWY_GIANT_TREE_TAIGA);
		//To fix Plains and Taiga's sorting
		//temp.sort(null);

		COLD_BIOMES = new ArrayList<EvoBiome>();
		COLD_BIOMES.add(TUNDRA);
		COLD_BIOMES.add(WOODED_TUNDRA);
		COLD_BIOMES.add(TAIGA);
		COLD_BIOMES.add(REDWOOD_TAIGA);
		COLD_BIOMES.sort(null);

		WARM_BIOMES = new ArrayList<EvoBiome>();
		WARM_BIOMES.add(PLAINS);
		WARM_BIOMES.add(BIRCH_FOREST);
		WARM_BIOMES.add(FOREST);
		WARM_BIOMES.add(ROOFED_FOREST);
		WARM_BIOMES.sort(null);

		HOT_BIOMES = new ArrayList<EvoBiome>();
		HOT_BIOMES.add(SAVANNA);
		HOT_BIOMES.add(PLAINS);
		HOT_BIOMES.add(FOREST);
		HOT_BIOMES.add(RAINFOREST);
		HOT_BIOMES.add(ROOFED_FOREST);
		HOT_BIOMES.sort(null);

		ARID_BIOMES = new ArrayList<EvoBiome>();
		ARID_BIOMES.add(DESERT);
		ARID_BIOMES.add(SAVANNA);
		ARID_BIOMES.add(PLAINS);
		ARID_BIOMES.add(RAINFOREST_ROOFED);
		ARID_BIOMES.add(JUNGLE);
		ARID_BIOMES.sort(null);
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
