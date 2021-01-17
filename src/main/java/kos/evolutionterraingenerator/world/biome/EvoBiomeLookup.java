package kos.evolutionterraingenerator.world.biome;

import java.util.ArrayList;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

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
	public final EvoBiome SONWY_GIANT_TREE_TAIGA;
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
    	PLAINS = new EvoBiome(lookupRegistry.get(BiomeList.PLAINS), new Biome[]
    			{lookupRegistry.get(BiomeList.PLAINS), lookupRegistry.get(BiomeList.SUNFLOWER_PLAINS)});
    	FOREST = new EvoBiome(lookupRegistry.get(BiomeList.FOREST), new Biome[]
    			{lookupRegistry.get(BiomeList.FOREST), lookupRegistry.get(BiomeList.FLOWER_FOREST)});
    	BIRCH_FOREST = new EvoBiome(lookupRegistry.get(BiomeList.BIRCH_FOREST), new Biome[]
    			{lookupRegistry.get(BiomeList.BIRCH_FOREST), lookupRegistry.get(BiomeList.TALL_BIRCH_FOREST)});
    	SNOWY_TUNDRA = new EvoBiome(lookupRegistry.get(BiomeList.SNOWY_TUNDRA), 0.125F, new Biome[]
    			{lookupRegistry.get(BiomeList.SNOWY_TUNDRA), lookupRegistry.get(BiomeList.SNOWY_TUNDRA), lookupRegistry.get(BiomeList.SNOWY_TUNDRA), lookupRegistry.get(BiomeList.ICE_SPIKES)});
    	MOUNTAINS = new EvoBiome(lookupRegistry.get(BiomeList.MOUNTAINS), new Biome[]
    			{lookupRegistry.get(BiomeList.MOUNTAINS), lookupRegistry.get(BiomeList.GRAVELLY_MOUNTAINS)});
    	WOODED_MOUNTAINS = new EvoBiome(lookupRegistry.get(BiomeList.WOODED_MOUNTAINS));
    	TAIGA = new EvoBiome(lookupRegistry.get(BiomeList.TAIGA));
    	SNOWY_TAIGA = new EvoBiome(lookupRegistry.get(BiomeList.SNOWY_TAIGA));
    	GIANT_TREE_TAIGA = new EvoBiome(lookupRegistry.get(BiomeList.DARK_FOREST), new Biome[]
    			{lookupRegistry.get(BiomeList.GIANT_TREE_TAIGA), lookupRegistry.get(BiomeList.GIANT_SPRUCE_TAIGA)});
    	DARK_FOREST = new EvoBiome(lookupRegistry.get(BiomeList.DARK_FOREST));
    	JUNGLE = new EvoBiome(lookupRegistry.get(BiomeList.JUNGLE), new Biome[]
    			{lookupRegistry.get(BiomeList.JUNGLE), lookupRegistry.get(BiomeList.BAMBOO_JUNGLE)});
    	SAVANNA = new EvoBiome(lookupRegistry.get(BiomeList.SAVANNA), new Biome[]
    			 {lookupRegistry.get(BiomeList.SAVANNA), lookupRegistry.get(BiomeList.SHATTERED_SAVANNA)});
    	DESERT = new EvoBiome(lookupRegistry.get(BiomeList.DESERT), new Biome[]
    			{lookupRegistry.get(BiomeList.DESERT), lookupRegistry.get(BiomeList.DESERT), lookupRegistry.get(BiomeList.BADLANDS)});
    	SONWY_GIANT_TREE_TAIGA = new EvoBiome(lookupRegistry.get(BiomeList.SNOWY_GIANT_TREE_TAIGA), new Biome[]
    			{lookupRegistry.get(BiomeList.SNOWY_GIANT_TREE_TAIGA), lookupRegistry.get(BiomeList.SNOWY_GIANT_SPRUCE_TAIGA)});
    	/*TUNDRA = new EvoBiome(NewlookupRegistry.get(TUNDRA, new Biome[]
    			{NewBiomes.TUNDRA, NewBiomes.GRAVELLY_TUNDRA});
    	WOODED_TUNDRA = new EvoBiome(NewBiomes.TUNDRA_WOODED);
    	RAINFOREST_ROOFED = new EvoBiome(NewBiomes.RAINFOREST, new Biome[]
    			{	NewBiomes.RAINFOREST, lookupRegistry.get(DARK_FOREST), 
    				NewBiomes.RAINFOREST, lookupRegistry.get(DARK_FOREST), 
    				NewBiomes.RAINFOREST, lookupRegistry.get(DARK_FOREST), 
    				NewBiomes.RAINFOREST, lookupRegistry.get(DARK_FOREST)	});
    	RAINFOREST = new EvoBiome(NewBiomes.RAINFOREST);*/
    	SAVANNA_PLAINS = new EvoBiome(lookupRegistry.get(BiomeList.PLAINS), new Biome[]
    			{	lookupRegistry.get(BiomeList.SAVANNA), lookupRegistry.get(BiomeList.PLAINS), 
    					lookupRegistry.get(BiomeList.SAVANNA), lookupRegistry.get(BiomeList.PLAINS), 
    					lookupRegistry.get(BiomeList.SAVANNA), lookupRegistry.get(BiomeList.PLAINS), 
    					lookupRegistry.get(BiomeList.SAVANNA), lookupRegistry.get(BiomeList.PLAINS),
    					lookupRegistry.get(BiomeList.SHATTERED_SAVANNA), lookupRegistry.get(BiomeList.SUNFLOWER_PLAINS)});
    	
    	HOT_SWAMP = new EvoBiome(lookupRegistry.get(BiomeList.SWAMP));
    	WARM_SWAMP = new EvoBiome(lookupRegistry.get(BiomeList.SWAMP));
    	COLD_SWAMP = new EvoBiome(lookupRegistry.get(BiomeList.SWAMP));
    	
    	COLD_ISLANDS = new EvoBiome(lookupRegistry.get(BiomeList.ICE_SPIKES));
    	
    	ISLAND_BIOMES = new EvoBiome(lookupRegistry.get(BiomeList.PLAINS), new Biome[]
    			{
    				lookupRegistry.get(BiomeList.PLAINS),
    				lookupRegistry.get(BiomeList.FOREST),
    				lookupRegistry.get(BiomeList.PLAINS),
    				lookupRegistry.get(BiomeList.FOREST),
    				lookupRegistry.get(BiomeList.PLAINS),
    				lookupRegistry.get(BiomeList.FOREST),
    			});
    	
    	HOT_ISLANDS = new EvoBiome(lookupRegistry.get(BiomeList.JUNGLE));
    }
    
	public void init()
	{
		SNOWY_BIOMES.clear();
		SNOWY_BIOMES.add(SNOWY_TUNDRA);
		SNOWY_BIOMES.add(SNOWY_TAIGA);
		SNOWY_BIOMES.add(SONWY_GIANT_TREE_TAIGA);
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
		HOT_BIOMES.add(DARK_FOREST);
		//HOT_BIOMES.sort(null);

		ARID_BIOMES.clear();
		ARID_BIOMES.add(DESERT);
		ARID_BIOMES.add(SAVANNA);
		//ARID_BIOMES.add(RAINFOREST_ROOFED);
		ARID_BIOMES.add(DARK_FOREST);
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

	public class EvoBiome 
	{
		private Biome defaultBiome;
		private Biome[] similarBiomes;		//int array of IDs of biomes similar to this
		private float downfall;
		
		public EvoBiome(Biome defaultBiome, float downfall, Biome[] similarBiomes)
		{
			this.defaultBiome = defaultBiome;
			this.similarBiomes = similarBiomes;
			this.downfall = downfall;
		}
	
		public Biome getBiome(double chance)
		{
			if (similarBiomes == null || similarBiomes.length == 0)
				return defaultBiome;
			return similarBiomes[(int)((similarBiomes.length - 1) * chance)];
		}
	
		public int compareTo(EvoBiome evoBiome)
		{
			if (this.downfall < evoBiome.downfall)
				return -1;
			if (this.downfall > evoBiome.downfall)
				return 1;
			return 0;
		}
		
		public EvoBiome(Biome defaultBiome, Biome[] similarBiomes) { this(defaultBiome, defaultBiome.getDownfall(), similarBiomes); }
		
		public EvoBiome(Biome biome) { this(biome, biome.getDownfall(), null); }
	
		public EvoBiome(Biome biome, float f) { this(biome, f, null); }
		
		public Biome getDefaultBiome() { return defaultBiome; }
	}
}
