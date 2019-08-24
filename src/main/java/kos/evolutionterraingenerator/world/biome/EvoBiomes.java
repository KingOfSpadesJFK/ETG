package kos.evolutionterraingenerator.world.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public abstract class EvoBiomes 
{
	private static final Biome[] PlainsArr = {Biomes.PLAINS, Biomes.MUTATED_PLAINS};
	private static final Biome[] ForestArr = {Biomes.FOREST, Biomes.MUTATED_FOREST};
	private static final Biome[] BirchForestArr = {Biomes.BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST};
	private static final Biome[] IcePlainsArr = {Biomes.ICE_PLAINS, Biomes.ICE_PLAINS, Biomes.ICE_PLAINS, Biomes.MUTATED_ICE_FLATS};
	private static final Biome[] TundraArr = {Biomes.EXTREME_HILLS, Biomes.MUTATED_EXTREME_HILLS};
	private static final Biome[] RedwoodArr = {Biomes.REDWOOD_TAIGA, Biomes.MUTATED_REDWOOD_TAIGA};
	private static final Biome[] SavannaArr = {Biomes.SAVANNA, Biomes.MUTATED_SAVANNA};
	private static final Biome[] DesertArr = {Biomes.DESERT, Biomes.DESERT, Biomes.MESA};
	
	public static final EvoBiome PLAINS = new EvoBiome(Biomes.PLAINS, PlainsArr);
	public static final EvoBiome FOREST = new EvoBiome(Biomes.FOREST, ForestArr);
	public static final EvoBiome BIRCH_FOREST = new EvoBiome(Biomes.BIRCH_FOREST, BirchForestArr);
	public static final EvoBiome ICE_PLAINS = new EvoBiome(Biomes.ICE_PLAINS, IcePlainsArr);
	public static final EvoBiome TUNDRA = new EvoBiome(Biomes.EXTREME_HILLS, TundraArr);
	public static final EvoBiome TAIGA = new EvoBiome(Biomes.TAIGA, null);
	public static final EvoBiome COLD_TAIGA = new EvoBiome(Biomes.COLD_TAIGA, null);
	public static final EvoBiome REDWOOD_TAIGA = new EvoBiome(Biomes.REDWOOD_TAIGA, RedwoodArr);
	public static final EvoBiome ROOFED_FOREST = new EvoBiome(Biomes.ROOFED_FOREST, null);
	public static final EvoBiome JUNGLE = new EvoBiome(Biomes.JUNGLE, null);
	public static final EvoBiome JUNGLE_EDGE = new EvoBiome(Biomes.JUNGLE_EDGE, null);
	public static final EvoBiome SAVANNA = new EvoBiome(Biomes.SAVANNA, SavannaArr);
	public static final EvoBiome DESERT = new EvoBiome(Biomes.DESERT, DesertArr);

	public static EvoBiome[] SNOWY_BIOMES = {EvoBiomes.ICE_PLAINS, EvoBiomes.COLD_TAIGA};
	public static EvoBiome[] COLD_BIOMES = {EvoBiomes.TUNDRA, EvoBiomes.TUNDRA, EvoBiomes.TAIGA, EvoBiomes.TAIGA, EvoBiomes.REDWOOD_TAIGA};
	public static EvoBiome[] WARM_BIOMES = {EvoBiomes.PLAINS, EvoBiomes.BIRCH_FOREST, EvoBiomes.FOREST, EvoBiomes.ROOFED_FOREST};
    public static EvoBiome[] HOT_BIOMES = {EvoBiomes.SAVANNA, EvoBiomes.PLAINS, EvoBiomes.FOREST, EvoBiomes.ROOFED_FOREST, EvoBiomes.JUNGLE};
    public static EvoBiome[] ARID_BIOMES = {EvoBiomes.DESERT, EvoBiomes.SAVANNA, EvoBiomes.SAVANNA, EvoBiomes.PLAINS, EvoBiomes.PLAINS, EvoBiomes.JUNGLE};
	
	public static void init()
	{
		ArrayList<EvoBiome> temp = new ArrayList<EvoBiome>();
		temp.add(EvoBiomes.ICE_PLAINS);
		temp.add(EvoBiomes.COLD_TAIGA);
		temp.sort(null);
		SNOWY_BIOMES = toArray(temp);
		temp.clear();
		
		temp.add(EvoBiomes.TUNDRA);
		temp.add(EvoBiomes.TAIGA);
		temp.add(EvoBiomes.REDWOOD_TAIGA);
		temp.sort(null);
		COLD_BIOMES = toArray(temp);
		temp.clear();
		
		temp.add(EvoBiomes.PLAINS);
		temp.add(EvoBiomes.BIRCH_FOREST);
		temp.add(EvoBiomes.FOREST);
		temp.add(EvoBiomes.ROOFED_FOREST);
		temp.sort(null);
		WARM_BIOMES = toArray(temp);
		temp.clear();
		
		temp.add(EvoBiomes.SAVANNA);
		temp.add(EvoBiomes.PLAINS);
		temp.add(EvoBiomes.FOREST);
		temp.add(EvoBiomes.ROOFED_FOREST);
		temp.add(EvoBiomes.JUNGLE);
		temp.sort(null);
		HOT_BIOMES = toArray(temp);
		temp.clear();

		temp.add(EvoBiomes.DESERT);
		temp.add(EvoBiomes.DESERT);
		temp.add(EvoBiomes.SAVANNA);
		temp.add(EvoBiomes.PLAINS);
		temp.add(EvoBiomes.JUNGLE);
		temp.sort(null);
		ARID_BIOMES = toArray(temp);
		temp.clear();
	}
	
	private static EvoBiome[] toArray(ArrayList<EvoBiome> list)
	{
		EvoBiome[] arr = new EvoBiome[list.size()];
		int i = 0;
		System.out.print("[");
		for (EvoBiome b : list)
		{
			arr[i] = b;
			System.out.print(b.getDefaultBiome().getBiomeName() + " (" + b.getDefaultBiome().getRainfall() + "), ");
			i++;
		}
		System.out.print("]");
		System.out.println();
		return arr;
	}
}
