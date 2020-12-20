package kos.evolutionterraingenerator.world.biome;

import java.util.ArrayList;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class EvoBiomeLookup 
{
	public final EvoBiome PLAINS;
	public final EvoBiome FOREST;
	public final EvoBiome BIRCH_FOREST;
	public final EvoBiome SNOWY_TUNDRA;
	public final EvoBiome MOUNTAINS;
	public final EvoBiome WOODED_MOUNTAINS;
	public final EvoBiome TAIGA;
	public final EvoBiome SNOWY_TAIGA;
	public final EvoBiome GIANT_TREE_TAIGA;
	public final EvoBiome DARK_FOREST;
	public final EvoBiome JUNGLE;
	public final EvoBiome SAVANNA;
	public final EvoBiome DESERT;
	//public final EvoBiome SONWY_GIANT_TREE_TAIGA;
	//public final EvoBiome TUNDRA;
	//public final EvoBiome WOODED_TUNDRA;
	//public final EvoBiome RAINFOREST_ROOFED;
	public final EvoBiome SAVANNA_PLAINS;
	
	public EvoBiome HOT_SWAMP;
	public EvoBiome WARM_SWAMP;
	public EvoBiome COLD_SWAMP;
	
	public EvoBiome COLD_ISLANDS;
	
	public EvoBiome ISLAND_BIOMES;
	
	public EvoBiome HOT_ISLANDS;

	public final ArrayList<EvoBiome> SNOWY_BIOMES = new ArrayList<EvoBiome>();
	public final ArrayList<EvoBiome> COLD_BIOMES = new ArrayList<EvoBiome>();
	public final ArrayList<EvoBiome> WARM_BIOMES = new ArrayList<EvoBiome>();
    public final ArrayList<EvoBiome> HOT_BIOMES = new ArrayList<EvoBiome>();
    public final ArrayList<EvoBiome> ARID_BIOMES = new ArrayList<EvoBiome>();
    
    public EvoBiomeLookup(Registry<Biome> lookupRegistry)
    {
    	PLAINS = new EvoBiome(lookupRegistry.getOrThrow(Biomes.PLAINS), new Biome[]
    			{lookupRegistry.getOrThrow(Biomes.PLAINS), lookupRegistry.getOrThrow(Biomes.SUNFLOWER_PLAINS)});
    	FOREST = new EvoBiome(lookupRegistry.getOrThrow(Biomes.FOREST), new Biome[]
    			{lookupRegistry.getOrThrow(Biomes.FOREST), lookupRegistry.getOrThrow(Biomes.FLOWER_FOREST)});
    	BIRCH_FOREST = new EvoBiome(lookupRegistry.getOrThrow(Biomes.BIRCH_FOREST), new Biome[]
    			{lookupRegistry.getOrThrow(Biomes.BIRCH_FOREST), lookupRegistry.getOrThrow(Biomes.TALL_BIRCH_FOREST)});
    	SNOWY_TUNDRA = new EvoBiome(lookupRegistry.getOrThrow(Biomes.SNOWY_TUNDRA), 0.125F, new Biome[]
    			{lookupRegistry.getOrThrow(Biomes.SNOWY_TUNDRA), lookupRegistry.getOrThrow(Biomes.SNOWY_TUNDRA), lookupRegistry.getOrThrow(Biomes.SNOWY_TUNDRA), lookupRegistry.getOrThrow(Biomes.ICE_SPIKES)});
    	MOUNTAINS = new EvoBiome(lookupRegistry.getOrThrow(Biomes.MOUNTAINS), new Biome[]
    			{lookupRegistry.getOrThrow(Biomes.MOUNTAINS), lookupRegistry.getOrThrow(Biomes.GRAVELLY_MOUNTAINS)});
    	WOODED_MOUNTAINS = new EvoBiome(lookupRegistry.getOrThrow(Biomes.WOODED_MOUNTAINS));
    	TAIGA = new EvoBiome(lookupRegistry.getOrThrow(Biomes.TAIGA));
    	SNOWY_TAIGA = new EvoBiome(lookupRegistry.getOrThrow(Biomes.SNOWY_TAIGA));
    	GIANT_TREE_TAIGA = new EvoBiome(lookupRegistry.getOrThrow(Biomes.DARK_FOREST), new Biome[]
    			{lookupRegistry.getOrThrow(Biomes.GIANT_TREE_TAIGA), lookupRegistry.getOrThrow(Biomes.GIANT_SPRUCE_TAIGA)});
    	DARK_FOREST = new EvoBiome(lookupRegistry.getOrThrow(Biomes.DARK_FOREST));
    	JUNGLE = new EvoBiome(lookupRegistry.getOrThrow(Biomes.JUNGLE), new Biome[]
    			{lookupRegistry.getOrThrow(Biomes.JUNGLE), lookupRegistry.getOrThrow(Biomes.BAMBOO_JUNGLE)});
    	SAVANNA = new EvoBiome(lookupRegistry.getOrThrow(Biomes.SAVANNA), new Biome[]
    			 {lookupRegistry.getOrThrow(Biomes.SAVANNA), lookupRegistry.getOrThrow(Biomes.SHATTERED_SAVANNA)});
    	DESERT = new EvoBiome(lookupRegistry.getOrThrow(Biomes.DESERT), new Biome[]
    			{lookupRegistry.getOrThrow(Biomes.DESERT), lookupRegistry.getOrThrow(Biomes.DESERT), lookupRegistry.getOrThrow(Biomes.BADLANDS)});
    	/*SONWY_GIANT_TREE_TAIGA = new EvoBiome(NewlookupRegistry.getOrThrow(SNOWY_GIANT_TREE_TAIGA, new Biome[]
    			{NewlookupRegistry.getOrThrow(SNOWY_GIANT_TREE_TAIGA, NewlookupRegistry.getOrThrow(SNOWY_GIANT_SPRUCE_TAIGA});
    	TUNDRA = new EvoBiome(NewlookupRegistry.getOrThrow(TUNDRA, new Biome[]
    			{NewBiomes.TUNDRA, NewBiomes.GRAVELLY_TUNDRA});
    	WOODED_TUNDRA = new EvoBiome(NewBiomes.TUNDRA_WOODED);
    	RAINFOREST_ROOFED = new EvoBiome(NewBiomes.RAINFOREST, new Biome[]
    			{	NewBiomes.RAINFOREST, lookupRegistry.getOrThrow(DARK_FOREST), 
    				NewBiomes.RAINFOREST, lookupRegistry.getOrThrow(DARK_FOREST), 
    				NewBiomes.RAINFOREST, lookupRegistry.getOrThrow(DARK_FOREST), 
    				NewBiomes.RAINFOREST, lookupRegistry.getOrThrow(DARK_FOREST)	});
    	RAINFOREST = new EvoBiome(NewBiomes.RAINFOREST);*/
    	SAVANNA_PLAINS = new EvoBiome(lookupRegistry.getOrThrow(Biomes.PLAINS), new Biome[]
    			{	lookupRegistry.getOrThrow(Biomes.SAVANNA), lookupRegistry.getOrThrow(Biomes.PLAINS), 
    					lookupRegistry.getOrThrow(Biomes.SAVANNA), lookupRegistry.getOrThrow(Biomes.PLAINS), 
    					lookupRegistry.getOrThrow(Biomes.SAVANNA), lookupRegistry.getOrThrow(Biomes.PLAINS), 
    					lookupRegistry.getOrThrow(Biomes.SAVANNA), lookupRegistry.getOrThrow(Biomes.PLAINS),
    					lookupRegistry.getOrThrow(Biomes.SHATTERED_SAVANNA), lookupRegistry.getOrThrow(Biomes.SUNFLOWER_PLAINS)});
    	
    	HOT_SWAMP = new EvoBiome(lookupRegistry.getOrThrow(Biomes.SWAMP));
    	WARM_SWAMP = new EvoBiome(lookupRegistry.getOrThrow(Biomes.SWAMP));
    	COLD_SWAMP = new EvoBiome(lookupRegistry.getOrThrow(Biomes.SWAMP));
    	
    	COLD_ISLANDS = new EvoBiome(lookupRegistry.getOrThrow(Biomes.ICE_SPIKES));
    	
    	ISLAND_BIOMES = new EvoBiome(lookupRegistry.getOrThrow(Biomes.PLAINS), new Biome[]
    			{
    				lookupRegistry.getOrThrow(Biomes.PLAINS),
    				lookupRegistry.getOrThrow(Biomes.FOREST),
    				lookupRegistry.getOrThrow(Biomes.PLAINS),
    				lookupRegistry.getOrThrow(Biomes.FOREST),
    				lookupRegistry.getOrThrow(Biomes.PLAINS),
    				lookupRegistry.getOrThrow(Biomes.FOREST),
    			});
    	
    	HOT_ISLANDS = new EvoBiome(lookupRegistry.getOrThrow(Biomes.JUNGLE));
    }
    
	public void init()
	{
		SNOWY_BIOMES.clear();
		SNOWY_BIOMES.add(SNOWY_TUNDRA);
		SNOWY_BIOMES.add(SNOWY_TAIGA);
		//SNOWY_BIOMES.add(SONWY_GIANT_TREE_TAIGA);
		//SNOWY_BIOMES.sort(null);

		COLD_BIOMES.clear();;
		COLD_BIOMES.add(MOUNTAINS);
		COLD_BIOMES.add(WOODED_MOUNTAINS);
		COLD_BIOMES.add(TAIGA);
		COLD_BIOMES.add(GIANT_TREE_TAIGA);
		//COLD_BIOMES.sort(null);

		WARM_BIOMES.clear();;
		WARM_BIOMES.add(PLAINS);
		WARM_BIOMES.add(BIRCH_FOREST);
		WARM_BIOMES.add(FOREST);
		WARM_BIOMES.add(DARK_FOREST);
		//WARM_BIOMES.sort(null);

		HOT_BIOMES.clear();
		HOT_BIOMES.add(SAVANNA);
		HOT_BIOMES.add(PLAINS);
		HOT_BIOMES.add(FOREST);
		//ARID_BIOMES.add(RAINFOREST_ROOFED);
		ARID_BIOMES.add(DARK_FOREST);
		//HOT_BIOMES.sort(null);

		ARID_BIOMES.clear();
		ARID_BIOMES.add(DESERT);
		ARID_BIOMES.add(SAVANNA);
		//ARID_BIOMES.add(RAINFOREST_ROOFED);
		WARM_BIOMES.add(DARK_FOREST);
		ARID_BIOMES.add(JUNGLE);
		//ARID_BIOMES.sort(null);
	}
	
	public EvoBiome[] toArray(ArrayList<EvoBiome> list)
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
