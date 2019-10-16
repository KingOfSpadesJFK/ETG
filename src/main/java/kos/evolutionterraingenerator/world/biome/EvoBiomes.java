package kos.evolutionterraingenerator.world.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import net.minecraft.init.Biomes;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeHills;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class EvoBiomes 
{
	private static final Biome[] PlainsArr = {Biomes.PLAINS, Biomes.MUTATED_PLAINS};
	private static final Biome[] ForestArr = {Biomes.FOREST, Biomes.MUTATED_FOREST};
	private static final Biome[] BirchForestArr = {Biomes.BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST};
	private static final Biome[] IcePlainsArr = {Biomes.ICE_PLAINS, Biomes.ICE_PLAINS, Biomes.ICE_PLAINS, Biomes.MUTATED_ICE_FLATS};
	private static final Biome[] TundraArr = {BiomeHandler.TUNDRA, BiomeHandler.GRAVELLY_TUNDRA};
	private static final Biome[] RedwoodArr = {Biomes.REDWOOD_TAIGA, Biomes.MUTATED_REDWOOD_TAIGA};
	private static final Biome[] SavannaArr = {Biomes.SAVANNA, Biomes.MUTATED_SAVANNA};
	private static final Biome[] DesertArr = {Biomes.DESERT, Biomes.DESERT, Biomes.MESA};
	private static final Biome[] SnowyRedwoodArr = {BiomeHandler.SNOWY_REDWOOD_TAIGA, BiomeHandler.MUTATED_SNOWY_REDWOOD_TAIGA};
	private static final Biome[] ExtremeArr = {Biomes.EXTREME_HILLS, Biomes.MUTATED_EXTREME_HILLS};
	private static final Biome[] RainforestArr =
		{	BiomeHandler.RAINFOREST, Biomes.ROOFED_FOREST, 
			BiomeHandler.RAINFOREST, Biomes.ROOFED_FOREST, 
			BiomeHandler.RAINFOREST, Biomes.ROOFED_FOREST, 
			BiomeHandler.RAINFOREST, Biomes.ROOFED_FOREST	};
	private static final Biome[] SavannaPlainsArr =
		{	Biomes.SAVANNA, Biomes.PLAINS, 
			Biomes.SAVANNA, Biomes.PLAINS, 
			Biomes.SAVANNA, Biomes.PLAINS, 
			Biomes.SAVANNA, Biomes.PLAINS,
			Biomes.MUTATED_SAVANNA, Biomes.MUTATED_PLAINS};
	
	public static final EvoBiome PLAINS = new EvoBiome(Biomes.PLAINS, PlainsArr);
	public static final EvoBiome FOREST = new EvoBiome(Biomes.FOREST, ForestArr);
	public static final EvoBiome BIRCH_FOREST = new EvoBiome(Biomes.BIRCH_FOREST, BirchForestArr);
	public static final EvoBiome ICE_PLAINS = new EvoBiome(Biomes.ICE_PLAINS, IcePlainsArr);
	public static final EvoBiome EXTREME_HILLS = new EvoBiome(Biomes.EXTREME_HILLS, TundraArr);
	public static final EvoBiome TAIGA = new EvoBiome(Biomes.TAIGA, null);
	public static final EvoBiome COLD_TAIGA = new EvoBiome(Biomes.COLD_TAIGA, null);
	public static final EvoBiome REDWOOD_TAIGA = new EvoBiome(Biomes.REDWOOD_TAIGA, RedwoodArr);
	public static final EvoBiome ROOFED_FOREST = new EvoBiome(Biomes.ROOFED_FOREST, null);
	public static final EvoBiome JUNGLE = new EvoBiome(Biomes.JUNGLE, null);
	public static final EvoBiome JUNGLE_EDGE = new EvoBiome(Biomes.JUNGLE_EDGE, null);
	public static final EvoBiome SAVANNA = new EvoBiome(Biomes.SAVANNA, SavannaArr);
	public static final EvoBiome DESERT = new EvoBiome(Biomes.DESERT, DesertArr);
	public static final EvoBiome SONWY_REDWOOD_TAIGA = new EvoBiome(BiomeHandler.SNOWY_REDWOOD_TAIGA, SnowyRedwoodArr);
	public static final EvoBiome TUNDRA = new EvoBiome(BiomeHandler.TUNDRA, TundraArr);
	public static final EvoBiome RAINFOREST_ROOFED = new EvoBiome(BiomeHandler.RAINFOREST, RainforestArr);
	public static final EvoBiome RAINFOREST = new EvoBiome(BiomeHandler.RAINFOREST, null);
	public static final EvoBiome SAVANNA_PLAINS = new EvoBiome(Biomes.PLAINS, SavannaPlainsArr);

	public static EvoBiome[] SNOWY_BIOMES = {EvoBiomes.ICE_PLAINS, EvoBiomes.COLD_TAIGA};
	public static EvoBiome[] COLD_BIOMES = {EvoBiomes.TUNDRA, EvoBiomes.TUNDRA, EvoBiomes.TAIGA, EvoBiomes.TAIGA, EvoBiomes.REDWOOD_TAIGA};
	public static EvoBiome[] WARM_BIOMES = {EvoBiomes.PLAINS, EvoBiomes.BIRCH_FOREST, EvoBiomes.FOREST, EvoBiomes.ROOFED_FOREST};
    public static EvoBiome[] HOT_BIOMES = {EvoBiomes.SAVANNA, EvoBiomes.PLAINS, EvoBiomes.FOREST, EvoBiomes.ROOFED_FOREST, EvoBiomes.JUNGLE};
    public static EvoBiome[] ARID_BIOMES = {EvoBiomes.DESERT, EvoBiomes.SAVANNA, EvoBiomes.SAVANNA, EvoBiomes.PLAINS, EvoBiomes.PLAINS, EvoBiomes.JUNGLE};
    
	public static void init()
	{
		ArrayList<EvoBiome> temp = new ArrayList<EvoBiome>();
		temp.add(ICE_PLAINS);
		temp.add(COLD_TAIGA);
		temp.add(SONWY_REDWOOD_TAIGA);
		//To fix Plains and Taiga's sorting
		//temp.sort(null);
		SNOWY_BIOMES = toArray(temp);
		temp.clear();
		
		temp.add(TUNDRA);
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
		temp.add(RAINFOREST_ROOFED);
		temp.add(JUNGLE);
		temp.sort(null);
		HOT_BIOMES = toArray(temp);
		temp.clear();

		temp.add(DESERT);
		temp.add(SAVANNA);
		temp.add(PLAINS);
		temp.add(RAINFOREST);
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
