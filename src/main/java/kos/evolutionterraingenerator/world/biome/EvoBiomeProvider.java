package kos.evolutionterraingenerator.world.biome;

import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import biomesoplenty.api.biome.BOPBiomes;
import kos.evolutionterraingenerator.core.EvolutionTerrainGenerator;
import kos.evolutionterraingenerator.util.OpenSimplexNoiseOctaves;

import java.util.List;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;

public class EvoBiomeProvider extends BiomeProvider
{
    public static final Codec<EvoBiomeProvider> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(
            Codec.LONG.fieldOf("seed").stable().forGetter((biomeProvider) -> biomeProvider.seed),
            RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter((biomeProvider) -> biomeProvider.lookupRegistry)
        ).apply(builder, builder.stable(EvoBiomeProvider::new));
    });
    
    private OpenSimplexNoiseOctaves tempOctave;
    private OpenSimplexNoiseOctaves humidOctave;
    private OpenSimplexNoiseOctaves landOctave;
	private OpenSimplexNoiseOctaves landOctave2;
    private OpenSimplexNoiseOctaves biomeChanceOctave;
    private OpenSimplexNoiseOctaves noiseOctave;
    private OpenSimplexNoiseOctaves mushroomOctave;
    private OpenSimplexNoiseOctaves islandOctave;
    private OpenSimplexNoiseOctaves riverOctave;
    private OpenSimplexNoiseOctaves riverOctave2;
    private OpenSimplexNoiseOctaves swampChance;
    private OpenSimplexNoiseOctaves swampType;
    private double landOffset;
    private final long seed;
    private static final List<RegistryKey<Biome>> biomes = ImmutableList.of(Biomes.OCEAN,
    		Biomes.PLAINS,
    		Biomes.DESERT,
    		Biomes.FOREST,
    		Biomes.TAIGA,
    		Biomes.SWAMP,
    		Biomes.RIVER,
    		Biomes.FROZEN_OCEAN,
    		Biomes.FROZEN_RIVER,
    		Biomes.SNOWY_TUNDRA,
    		Biomes.SNOWY_MOUNTAINS,
    		Biomes.MUSHROOM_FIELDS,
    		Biomes.MUSHROOM_FIELD_SHORE,
    		Biomes.BEACH,
    		Biomes.DESERT_HILLS,
    		Biomes.WOODED_HILLS,
    		Biomes.TAIGA_HILLS,
    		Biomes.MOUNTAIN_EDGE,
    		Biomes.JUNGLE,
    		Biomes.JUNGLE_HILLS,
    		Biomes.JUNGLE_EDGE,
    		Biomes.DEEP_OCEAN,
    		Biomes.STONE_SHORE,
    		Biomes.SNOWY_BEACH,
    		Biomes.BIRCH_FOREST,
    		Biomes.BIRCH_FOREST_HILLS,
    		Biomes.DARK_FOREST,
    		Biomes.SNOWY_TAIGA,
    		Biomes.SNOWY_TAIGA_HILLS,
    		Biomes.GIANT_TREE_TAIGA,
    		Biomes.GIANT_TREE_TAIGA_HILLS,
    		Biomes.WOODED_MOUNTAINS,
    		Biomes.SAVANNA,
    		Biomes.SAVANNA_PLATEAU,
    		Biomes.BADLANDS,
    		Biomes.WOODED_BADLANDS_PLATEAU,
    		Biomes.BADLANDS_PLATEAU,
    		Biomes.WARM_OCEAN,
    		Biomes.LUKEWARM_OCEAN,
    		Biomes.COLD_OCEAN,
    		Biomes.DEEP_WARM_OCEAN,
    		Biomes.DEEP_LUKEWARM_OCEAN,
    		Biomes.DEEP_COLD_OCEAN,
    		Biomes.DEEP_FROZEN_OCEAN,
    		Biomes.SUNFLOWER_PLAINS,
    		Biomes.DESERT_LAKES,
    		Biomes.GRAVELLY_MOUNTAINS,
    		Biomes.FLOWER_FOREST,
    		Biomes.TAIGA_MOUNTAINS,
    		Biomes.SWAMP_HILLS,
    		Biomes.ICE_SPIKES,
    		Biomes.MODIFIED_JUNGLE,
    		Biomes.MODIFIED_JUNGLE_EDGE,
    		Biomes.TALL_BIRCH_FOREST,
    		Biomes.TALL_BIRCH_HILLS,
    		Biomes.DARK_FOREST_HILLS,
    		Biomes.SNOWY_TAIGA_MOUNTAINS,
    		Biomes.GIANT_SPRUCE_TAIGA,
    		Biomes.GIANT_SPRUCE_TAIGA_HILLS,
    		Biomes.MODIFIED_GRAVELLY_MOUNTAINS,
    		Biomes.SHATTERED_SAVANNA,
    		Biomes.SHATTERED_SAVANNA_PLATEAU,
    		Biomes.ERODED_BADLANDS,
    		Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU,
    		Biomes.MODIFIED_BADLANDS_PLATEAU);

	public static final double SNOW_TEMP = 0.125;
	public static final double COLD_TEMP = 0.375;
	public static final double WARM_TEMP = 0.625;
	public static final double HOT_TEMP = 0.875;
	
	private double humidityScale = 1.0;
	private double chanceScale = 1.0;
	
	private EvoBiomeProviderSettings providerSettings;
	private final Registry<Biome> lookupRegistry;
	private EvoBiomeLookup evoBiomes;
	
	public EvoBiomeProvider(long seed, Registry<Biome> lookupRegistry)
	{
		this(new EvoBiomeProviderSettings(), seed, lookupRegistry);
	}

	public EvoBiomeProvider(EvoBiomeProviderSettings settingsProvider, long seed, Registry<Biome> lookupRegistry) {
		super(biomes.stream().map((key) -> {
	         return () -> {
	            return lookupRegistry.getOrThrow(key);
	         };
	      }));
		
		this.evoBiomes = new EvoBiomeLookup(lookupRegistry);
		this.lookupRegistry = lookupRegistry;
		this.seed = seed;
        SharedSeedRandom rand = new SharedSeedRandom(seed);
        this.landOctave = new OpenSimplexNoiseOctaves(rand, oceanOctaves);
		this.landOctave2 = new OpenSimplexNoiseOctaves(rand, oceanOctaves);
		this.riverOctave = new OpenSimplexNoiseOctaves(rand, 8);
		this.riverOctave2 = new OpenSimplexNoiseOctaves(rand, 8);
		this.tempOctave = new OpenSimplexNoiseOctaves(rand, 8);
        this.humidOctave = new OpenSimplexNoiseOctaves(rand, 8);
		this.biomeChanceOctave = new OpenSimplexNoiseOctaves(rand, 4);
		this.mushroomOctave = new OpenSimplexNoiseOctaves(rand, 4);
		this.islandOctave = new OpenSimplexNoiseOctaves(rand, 4);
		this.noiseOctave = new OpenSimplexNoiseOctaves(rand, 2);
        this.swampChance = new OpenSimplexNoiseOctaves(rand, 4);
        this.swampType = new OpenSimplexNoiseOctaves(rand, 4);
		this.providerSettings = settingsProvider;
		this.landOffset = 0.0;
		if (landOctave2.getNoise(0.0, 0.0) * 0.125 / (double)oceanOctaves < oceanThreshold)
		{
			while (landOctave.getNoise(0.0, landOffset, 0.0) * 0.125 / (double)oceanOctaves < oceanThreshold)
				landOffset += 1.0;
		}
		
		evoBiomes.init();
		/*
		try
		{
			if (providerSettings.isUseBOPBiomes())
			{
				humidityScale = 1.0;
				chanceScale = 1.0;
				BOPSupport.setup(biomes);
			}
		}
		catch (Exception e)
		{
			humidityScale = 1.0;
			chanceScale = 1.0;
			EvoBiomes.init();
			this.providerSettings.setUseBOPBiomes(false);
			EvolutionTerrainGenerator.logger.error("ETG: Biomes O' Plenty support broken! Check if the right version of any of the mods are listed");
		}
		*/
	}

	public static final int oceanOctaves = 8;
    public static final double biomeScale = 3.0;
    public static final double oceanScale = 0.0375;
    public static final double oceanThreshold = 0.5;
    public static final double beachThreshold = 0.01;
    public static final double deepThreshold = 0.05;
    
    public static final double riverThreshold = 0.025;
    public static final double riverMidPoint = 0.0;
    public static final double riverScale = 4.0;
    
    public double[] getTemperature(double x, double z)
    {
    	double noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (tempOctave.getNoise(x * (0.0875 / biomeScale), z * (0.0875 / biomeScale)) * 0.00625 + 0.5) * 0.99;
    	double[] arr =
    		{
    				MathHelper.clamp(d0 + 0.01, 0.0, 1.0),
    				MathHelper.clamp(d0 + noise * 0.01, 0.0, 1.0),
    		};
    	return arr;
    }
    
    public double[] getHumidity(double x, double z)
    {
    	double noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (humidOctave.getNoise((double)x * (0.3 / biomeScale / humidityScale), (double)z * (0.3 / biomeScale / humidityScale)) * 0.0075 + 0.5) * 0.95;
    	double[] arr =
    		{
				MathHelper.clamp(d0 + 0.05, 0.0, 1.0),
    			MathHelper.clamp(d0 + noise * 0.05, 0.0, 1.0),
    		};
    	return arr;
    }
    
    protected double[] getBiomeChance(double x, double z)
    {
    	double noise = noiseOctave.getNoise((double)x * 0.25, (double)z * 0.25) * 1.1 + 0.5;
    	double d0 = (biomeChanceOctave.getNoise((double)x * 0.005 / chanceScale, (double)z * 0.005 / chanceScale) * 0.05 + 0.5) * 0.99;
    	double[] arr =
    		{
    				MathHelper.clamp(d0 + 0.01, 0.0, 1.0),
    				MathHelper.clamp(d0 + noise * 0.01, 0.0, 1.0),
    		};
    	return arr;
    }
    
    public boolean getRiver(int x, int z)
    {
    	double riverChance = riverOctave.getNoise((double)x * (0.025 / riverScale), (double)z * (0.025 / riverScale)) * 0.125;
    	double riverChance2 = riverOctave2.getNoise((double)x * (0.025 / riverScale), (double)z * (0.025 / riverScale)) * 0.125;
    	return (riverChance >= riverMidPoint - riverThreshold && riverChance <= riverMidPoint + riverThreshold) ||
    			(riverChance2 >= riverMidPoint - riverThreshold && riverChance2 <= riverMidPoint + riverThreshold);
    }
    
    public double[] getLandmass(double x, double z)
    {
    	double landmass1 = landOctave.getNoise((double)x * (0.00125 / oceanScale), landOffset, (double)z * (0.00125 / oceanScale))  * 0.125 / (double)oceanOctaves;
    	double landmass2 = landOctave2.getNoise((double)x * (0.00125 / oceanScale), (double)z * (0.00125 / oceanScale)) * 0.125 / (double)oceanOctaves;
    	double mushroomChance = (mushroomOctave.getNoise(x * (0.00375 / biomeScale), z * (0.00375 / biomeScale)) - 8.0) * 2.0 / (double)oceanOctaves;
    	double islandChance = (islandOctave.getNoise(x * (0.02 / biomeScale), z * (0.02 / biomeScale)) - 3.25) / (double)oceanOctaves;

		double domLand = landmass1;
		if (domLand < landmass2)
			domLand = landmass2;
		if (domLand < islandChance)
			domLand = islandChance;
		if (domLand < mushroomChance)
			domLand = mushroomChance;
		
    	return new double[]{landmass1, landmass2, islandChance, mushroomChance, domLand};
    }
    
    public Biome[] generateLandBiome(double x, double z)
    {
        double[] temperature = getTemperature(x, z);
        double[] humidity = getHumidity(x, z);
		double[] biomeChance = getBiomeChance(x, z);

		return new Biome[] 
				{
						getLandBiome(temperature[0], humidity[0], biomeChance[0]),
						getLandBiome(temperature[1], humidity[1], biomeChance[1])
				};
    }
    
    public Biome generateLandBiome(double x, double z, boolean useNoise)
    {
        double temperature = useNoise ? getTemperature(x, z)[1] : getTemperature(x, z)[0];
        double humidity = useNoise ? getHumidity(x, z)[1] : getHumidity(x, z)[0];
		double biomeChance = useNoise ? getBiomeChance(x, z)[1] : getBiomeChance(x, z)[0];

		return getLandBiome(temperature, humidity, biomeChance);
    }
    
	private static final int BEACH_SAMPLES = 4;
	private static final double BEACH_SEARCH_SCALE = 2.0;
	public boolean canBeBeach(double x, double z)
	{
    	double landmass1 = 0;
    	double landmass2 = 0;
		double xO = 0;
		double zO = 0;
		
    	for (int i = 0; i <= BEACH_SAMPLES; i++)
    	{
    		for (int j = 0; j <= BEACH_SAMPLES; j ++)
    		{
    			xO = (double)(i - BEACH_SAMPLES / 2) * BEACH_SEARCH_SCALE + x;
    			zO = (double)(j - BEACH_SAMPLES / 2) * BEACH_SEARCH_SCALE + z;
    	    	landmass1 = landOctave.getNoise(xO * (0.00125 / oceanScale), landOffset, zO * (0.00125 / oceanScale))
    	    			* 0.125 / (double)oceanOctaves;
    	    	landmass2 = landOctave2.getNoise(xO * (0.00125 / oceanScale), zO * (0.00125 / oceanScale)) 
    	    			* 0.125 / (double)oceanOctaves;
    	    	
    	    	if (landmass1 < oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale && 
    	    			landmass2 < oceanThreshold - beachThreshold / (double)oceanOctaves / oceanScale)
    	    		return true;
    		}
    	}
    	
    	return false;
	}

    @Override
    public Biome getNoiseBiome(int x, int y, int z) 
    {
    	return getNoiseBiome(x << 2, y << 2, z << 2, true); 
    }
    
    public Biome getNoiseBiome(int x, int y, int z, boolean useNoise) 
    {
    	Biome biome = generateLandBiome((double)x, (double)z, useNoise);
    	return setBiomebyHeight(biome, x, y, z, useNoise);
    }
    
    public Biome[] getBiomesByHeight(int x, int y, int z)
    {
        double[] temperature = getTemperature(x, z);
        double[] humidity = getHumidity(x, z);
		double[] biomeChance = getBiomeChance(x, z);
		Biome[] biomes = new Biome[2];
		
		biomes[0] = setBiomebyHeight(getLandBiome(temperature[0], humidity[0], biomeChance[0]), x, y, z, temperature[0], humidity[0], biomeChance[0]);
		biomes[1] = setBiomebyHeight(getLandBiome(temperature[1], humidity[1], biomeChance[1]), x, y, z, temperature[1], humidity[1], biomeChance[1]);
		
		return biomes;
    }
    
    public Biome setBiomebyHeight(Biome biome, int x, int y, int z, boolean useNoise)
    {
        double temperature = useNoise ? getTemperature(x, z)[1] : getTemperature(x, z)[0];
        double humidity = useNoise ? getHumidity(x, z)[1] : getHumidity(x, z)[0];
		double biomeChance = useNoise ? getBiomeChance(x, z)[1] : getBiomeChance(x, z)[0];
		
		return setBiomebyHeight(biome, x, y, z, temperature, humidity, biomeChance);
    }
    
    public Biome[] setBiomebyHeight(Biome[] biome, int x, int y, int z)
    {
        double[] temperature = getTemperature(x, z);
        double[] humidity = getHumidity(x, z);
		double[] biomeChance = getBiomeChance(x, z);
		
		biome[0] = setBiomebyHeight(biome[0], x, y, z, temperature[0], humidity[0], biomeChance[0]);
		biome[1] = setBiomebyHeight(biome[1], x, y, z, temperature[1], humidity[1], biomeChance[1]);
		
		return biome;
    }
    
    private Biome setBiomebyHeight(Biome biome, int x, int y, int z, double temperature, double humidity, double biomeChance)
    {
        int seaLevel = this.providerSettings.getSeaLevel();
   	 	double[] landmass = getLandmass(x, z);
   	 	double beachThreshold = oceanThreshold - EvoBiomeProvider.beachThreshold / (double)oceanOctaves / oceanScale;
		boolean isOcean = landmass[4] < beachThreshold;
		boolean isBeach = !isOcean && (landmass[4] < EvoBiomeProvider.oceanThreshold) && canBeBeach(x, z);
		boolean isSpecialIsland = landmass[0] < EvoBiomeProvider.oceanThreshold && landmass[1] < EvoBiomeProvider.oceanThreshold;
		
    	if (landmass[2] == landmass[4] && isSpecialIsland)
    	{
			if (temperature < EvoBiomeProvider.SNOW_TEMP)
				biome = evoBiomes.COLD_ISLANDS.getBiome(biomeChance);
			else if (temperature < EvoBiomeProvider.HOT_TEMP)
				biome = evoBiomes.ISLAND_BIOMES.getBiome(biomeChance);
			else
				biome = evoBiomes.HOT_ISLANDS.getBiome(biomeChance);
    	}
    	if (landmass[3] == landmass[4] && isSpecialIsland)
			biome = getBiome(Biomes.MUSHROOM_FIELDS);
        
        if (isBeach || isOcean)
        {
        	if (y < seaLevel)
        		return getOcean(temperature, y < 40);
        	if (y < seaLevel + 3)
        	{
        		if (getSettings().isUseBOPBiomes()) 
        		{
        			if ((landmass[0] == landmass[4] || landmass[2] == landmass[4]) && 
    				(biome.equals(getBiome(Biomes.JUNGLE)) ||
    						biome.equals(getBiome(Biomes.BAMBOO_JUNGLE)) ||
    						biome.equals(getBiome(BOPBiomes.tropics))
    						)
    				) 
        				return getBiome(BOPBiomes.tropic_beach);
            		if (biome.equals(getBiome(BOPBiomes.volcano)))
            			return getBiome(BOPBiomes.volcanic_plains);
            		if (biome.equals(getBiome(BOPBiomes.origin_valley)))
            			return getBiome(Biomes.BEACH);
        		}
	    		if (!biome.equals(getBiome(Biomes.BADLANDS)) && 
	    				!biome.equals(getBiome(Biomes.MUSHROOM_FIELDS)) && 
	    				!biome.equals(getBiome(Biomes.DESERT)) && 
	    				!(getSettings().isUseBOPBiomes() && 
	    						(biome.equals(getBiome(BOPBiomes.wasteland)) )
	    						)
	    				)
	    			return getBeach(x, z);
        	}
        }
        
        if (isSpecialIsland && landmass[2] == landmass[4] || landmass[3] == landmass[4])
        	return biome;

        double swampChance = this.swampChance.getNoise((double)x * 0.0125, (double)z * 0.0125);
        swampChance = MathHelper.clamp(swampChance, 0.0, 1.0);
    	if (temperature > 0.5 && humidity > 0.675 && swampChance < 0.375 - 0.25 * ((MathHelper.clamp(temperature, 0.5, 1.0) - 0.5) * 2.0) && y <= seaLevel + 3)
    	{
            double swampType = this.swampType.getNoise((double)x * 0.0125, (double)z * 0.0125) * 0.125 + 0.5;
            swampType = MathHelper.clamp(swampType, 0.0, 1.0);
            Biome swamp = null;
            if (temperature < EvoBiomeProvider.WARM_TEMP)
            	swamp = evoBiomes.COLD_SWAMP.getBiome(swampType);
            else if (temperature < EvoBiomeProvider.HOT_TEMP)
              	swamp = evoBiomes.WARM_SWAMP.getBiome(swampType);
            else
            	swamp = evoBiomes.HOT_SWAMP.getBiome(swampType);
            
            if (swamp != null)
            	biome = swamp;
    	}
    	if (biome.equals(getBiome(Biomes.BADLANDS)))
    	{
    		if (y >= seaLevel + 50)
        		biome = getBiome(Biomes.WOODED_BADLANDS_PLATEAU);
    	}
    	/*if (getSettings().isUseBOPBiomes() && temperature < EvoBiomeProvider.SNOW_TEMP && !biome.equals(Biomes.ICE_SPIKES))
    	{
    		if (y >= seaLevel + 65)
    			biome = BOPBiomes.alps.get();
    		else if (y >= seaLevel + 50)
    			biome = BOPBiomes.alps_foothills.get();
    	}*/
    	return biome;
    }

    public Biome getBiome(RegistryKey<Biome> biome)
    {
    	return this.lookupRegistry.getOrThrow(biome);
	}

	public Biome getLandBiome(double temp, double humid, double chance)
    {
    	EvoBiome[] arr = evoBiomes.WARM_BIOMES.toArray(new EvoBiome[evoBiomes.WARM_BIOMES.size()]);
    	
		if (temp < SNOW_TEMP)
			arr = evoBiomes.SNOWY_BIOMES.toArray(new EvoBiome[evoBiomes.SNOWY_BIOMES.size()]);
		else if (temp < COLD_TEMP)
			arr = evoBiomes.COLD_BIOMES.toArray(new EvoBiome[evoBiomes.COLD_BIOMES.size()]);
		else if (temp < WARM_TEMP)
			arr = evoBiomes.WARM_BIOMES.toArray(new EvoBiome[evoBiomes.WARM_BIOMES.size()]);
		else if (temp < HOT_TEMP)
			arr = evoBiomes.HOT_BIOMES.toArray(new EvoBiome[evoBiomes.HOT_BIOMES.size()]);
		else
			arr = evoBiomes.ARID_BIOMES.toArray(new EvoBiome[evoBiomes.ARID_BIOMES.size()]);
		
		return arr[(int)((arr.length - 1) * humid)].getBiome(chance);
    }

	public Biome getBeach(double x, double z)
	{
		double temp = getTemperature(x, z)[1];
		double humid = getHumidity(x, z)[1];
		double chance = getBiomeChance(x, z)[1];
		double[] landmass = getLandmass(x, z);
		return getBeach(temp, humid, 
				landmass[0] < landmass[1], 
				getLandBiome(temp, humid, chance).getDownfall() <= 0.0F);
	}

    public Biome getBeach(double temp, double humid, boolean isGravel, boolean noDownfall)
    {
    	if (temp < SNOW_TEMP)
    	{
    		//if (isGravel)
    			//return NewBiomes.SNOWY_GRAVEL_BEACH;
    		return getBiome(Biomes.SNOWY_BEACH);
    	}
    	if (noDownfall)
    	{
        	//if (isGravel)
        		//return NewBiomes.DRY_GRAVEL_BEACH;
    		//return NewBiomes.DRY_BEACH;
    	}
    	//if (isGravel)
    		//return NewBiomes.GRAVEL_BEACH;
    	return getBiome(Biomes.BEACH);
    }
    
    public Biome getOcean(double temp, boolean deep)
    {
    	if (deep)
    	{		
    		if (temp < SNOW_TEMP)
    			return getBiome(Biomes.DEEP_FROZEN_OCEAN);
    		else if (temp < COLD_TEMP)
    			return getBiome(Biomes.DEEP_COLD_OCEAN);
    		else if (temp < WARM_TEMP)
    			return getBiome(Biomes.DEEP_LUKEWARM_OCEAN);
    		else
    			return getBiome(Biomes.DEEP_WARM_OCEAN);
    	}
		if (temp < SNOW_TEMP)
			return getBiome(Biomes.FROZEN_OCEAN);
		else if (temp < COLD_TEMP)
			return getBiome(Biomes.COLD_OCEAN);
		else if (temp < WARM_TEMP)
			return getBiome(Biomes.LUKEWARM_OCEAN);
		else
			return getBiome(Biomes.WARM_OCEAN);
    }
    
	public EvoBiomeProviderSettings getSettings() 
	{
		return this.providerSettings;
	}

	@Override
	protected Codec<? extends BiomeProvider> getBiomeProviderCodec() { return CODEC;	}

	@Override
	public EvoBiomeProvider getBiomeProvider(long seed)
	{
		return new EvoBiomeProvider(providerSettings, seed, lookupRegistry);
	}

	static {
	    Registry.register(Registry.BIOME_PROVIDER_CODEC, new ResourceLocation(EvolutionTerrainGenerator.MODID, "biome_source"), CODEC);
	}

	public IObjectIntIterable<Biome> getRegistry() { return lookupRegistry; }
}
