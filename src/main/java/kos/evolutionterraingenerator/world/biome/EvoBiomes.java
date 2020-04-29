package kos.evolutionterraingenerator.world.biome;

import java.util.ArrayList;

import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;

public abstract class EvoBiomes 
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
	
	public static final EvoBiome PLAINS = new EvoBiome(Biomes.PLAINS, PlainsArr);
	public static final EvoBiome FOREST = new EvoBiome(Biomes.FOREST, ForestArr);
	public static final EvoBiome BIRCH_FOREST = new EvoBiome(Biomes.BIRCH_FOREST, BirchForestArr);
	public static final EvoBiome ICE_PLAINS = new EvoBiome(Biomes.SNOWY_TUNDRA, IcePlainsArr);
	public static final EvoBiome EXTREME_HILLS = new EvoBiome(Biomes.MOUNTAINS, ExtremeArr);
	public static final EvoBiome TAIGA = new EvoBiome(Biomes.TAIGA, null);
	public static final EvoBiome COLD_TAIGA = new EvoBiome(Biomes.SNOWY_TAIGA, null);
	public static final EvoBiome REDWOOD_TAIGA = new EvoBiome(Biomes.DARK_FOREST, RedwoodArr);
	public static final EvoBiome ROOFED_FOREST = new EvoBiome(Biomes.DARK_FOREST, null);
	public static final EvoBiome JUNGLE = new EvoBiome(Biomes.JUNGLE, JungleArr);
	public static final EvoBiome JUNGLE_EDGE = new EvoBiome(Biomes.JUNGLE_EDGE, null);
	public static final EvoBiome SAVANNA = new EvoBiome(Biomes.SAVANNA, SavannaArr);
	public static final EvoBiome DESERT = new EvoBiome(Biomes.DESERT, DesertArr);
	public static final EvoBiome SONWY_GIANT_TREE_TAIGA = new EvoBiome(NewBiomes.SNOWY_GIANT_TREE_TAIGA, SnowyRedwoodArr);
	public static final EvoBiome TUNDRA = new EvoBiome(NewBiomes.TUNDRA, TundraArr);
	public static final EvoBiome WOODED_TUNDRA = new EvoBiome(NewBiomes.TUNDRA_WOODED, null);
	public static final EvoBiome RAINFOREST_ROOFED = new EvoBiome(NewBiomes.RAINFOREST, RainforestArr);
	public static final EvoBiome RAINFOREST = new EvoBiome(NewBiomes.RAINFOREST, null);
	public static final EvoBiome SAVANNA_PLAINS = new EvoBiome(Biomes.PLAINS, SavannaPlainsArr);

	public static EvoBiome[] SNOWY_BIOMES = {EvoBiomes.ICE_PLAINS, EvoBiomes.COLD_TAIGA};
	public static EvoBiome[] COLD_BIOMES = {EvoBiomes.EXTREME_HILLS, EvoBiomes.EXTREME_HILLS, EvoBiomes.TAIGA, EvoBiomes.TAIGA, EvoBiomes.REDWOOD_TAIGA};
	public static EvoBiome[] WARM_BIOMES = {EvoBiomes.PLAINS, EvoBiomes.BIRCH_FOREST, EvoBiomes.FOREST, EvoBiomes.ROOFED_FOREST};
    public static EvoBiome[] HOT_BIOMES = {EvoBiomes.SAVANNA, EvoBiomes.PLAINS, EvoBiomes.FOREST, EvoBiomes.ROOFED_FOREST, EvoBiomes.RAINFOREST};
    public static EvoBiome[] ARID_BIOMES = {EvoBiomes.DESERT, EvoBiomes.SAVANNA, EvoBiomes.SAVANNA, EvoBiomes.PLAINS, EvoBiomes.PLAINS, EvoBiomes.RAINFOREST, EvoBiomes.RAINFOREST, EvoBiomes.JUNGLE};
    
	public static void init()
	{
		ArrayList<EvoBiome> temp = new ArrayList<EvoBiome>();
		temp.add(ICE_PLAINS);
		temp.add(COLD_TAIGA);
		temp.add(SONWY_GIANT_TREE_TAIGA);
		//To fix Plains and Taiga's sorting
		//temp.sort(null);
		SNOWY_BIOMES = toArray(temp);
		temp.clear();
		
		temp.add(TUNDRA);
		temp.add(WOODED_TUNDRA);
		temp.add(TAIGA);
		temp.add(REDWOOD_TAIGA);
		temp.sort(null);
		COLD_BIOMES = toArray(temp);
		temp.clear();
		
		temp.add(PLAINS);
		temp.add(BIRCH_FOREST);
		temp.add(FOREST);
		temp.add(ROOFED_FOREST);
		temp.sort(null);
		WARM_BIOMES = toArray(temp);
		temp.clear();

		temp.add(SAVANNA);
		temp.add(PLAINS);
		temp.add(FOREST);
		temp.add(RAINFOREST);
		temp.add(ROOFED_FOREST);
		temp.sort(null);
		HOT_BIOMES = toArray(temp);
		temp.clear();

		temp.add(DESERT);
		temp.add(SAVANNA);
		temp.add(PLAINS);
		temp.add(RAINFOREST_ROOFED);
		temp.add(JUNGLE);
		temp.sort(null);
		ARID_BIOMES = toArray(temp);
		temp.clear();
	}
	
	private static EvoBiome[] toArray(ArrayList<EvoBiome> list)
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
